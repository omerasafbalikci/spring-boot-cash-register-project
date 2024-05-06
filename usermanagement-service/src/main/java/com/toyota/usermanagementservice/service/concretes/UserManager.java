package com.toyota.usermanagementservice.service.concretes;

import com.toyota.usermanagementservice.dao.UserRepository;
import com.toyota.usermanagementservice.domain.Role;
import com.toyota.usermanagementservice.domain.User;
import com.toyota.usermanagementservice.dto.requests.CreateUserRequest;
import com.toyota.usermanagementservice.dto.requests.RegisterRequest;
import com.toyota.usermanagementservice.dto.requests.UpdateUserRequest;
import com.toyota.usermanagementservice.dto.responses.GetAllUsersResponse;
import com.toyota.usermanagementservice.service.abstracts.UserService;
import com.toyota.usermanagementservice.service.rules.UserBusinessRules;
import com.toyota.usermanagementservice.utilities.exceptions.*;
import com.toyota.usermanagementservice.utilities.mappers.ModelMapperService;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class UserManager implements UserService {
    private final UserRepository userRepository;
    private final Logger logger = LogManager.getLogger(UserService.class);
    private final WebClient.Builder webClientBuilder;
    private final ModelMapperService modelMapperService;
    private final UserBusinessRules userBusinessRules;

    @Override
    public GetAllUsersResponse createUser(CreateUserRequest createUserRequest) {
        if (this.userRepository.existsByUsernameAndDeletedIsFalse(createUserRequest.getUsername())) {
            throw new UserAlreadyExistsException("Username '" + createUserRequest.getUsername() + "' is already taken");
        }
        if (this.userRepository.existsByEmailAndDeletedIsFalse(createUserRequest.getEmail())) {
            throw new UserAlreadyExistsException("Email '" + createUserRequest.getEmail() + "' is already taken");
        }
        User user = this.modelMapperService.forRequest().map(createUserRequest, User.class);
        Set<String> roles = createUserRequest.getRoles().stream().map(Enum::toString).collect(Collectors.toSet());

        Boolean response = this.webClientBuilder.build().post()
                .uri("http://authenticationauthorization-service/auth/register")
                .bodyValue(new RegisterRequest(createUserRequest.getUsername(), createUserRequest.getPassword(), roles))
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse -> {
                    if (clientResponse.statusCode() == HttpStatus.CONFLICT) {
                        throw new UserAlreadyExistsException("User already exists in authentication-authorization-service");
                    } else if (clientResponse.statusCode() == HttpStatus.BAD_REQUEST) {
                        throw new RoleNotFoundException("Problem with roles in authentication-authorization-service");
                    } else {
                        throw new UnexpectedException("Unexpected exception in authentication-authorization-service");
                    }
                })
                .bodyToMono(Boolean.class)
                .block();
        if (response == null || !response) {
            throw new UnexpectedException("Failed to create user! Reason: Unexpected problem in authentication-authorization-service");
        }
        User saved = this.userRepository.save(user);
        return this.modelMapperService.forResponse().map(saved, GetAllUsersResponse.class);
    }

    @Override
    public GetAllUsersResponse updateUser(UpdateUserRequest updateUserRequest) {
        Optional<User> optionalUser = this.userRepository.findById(updateUserRequest.getId());

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (updateUserRequest.getEmail() != null && !user.getEmail().equals(updateUserRequest.getEmail())) {
                if (this.userRepository.existsByEmailAndDeletedIsFalse(updateUserRequest.getEmail())) {
                    throw new UserAlreadyExistsException("Email is already taken");
                }
                user.setEmail(updateUserRequest.getEmail());
            }
            if (updateUserRequest.getUsername() != null && !user.getUsername().equals(updateUserRequest.getUsername())) {
                if (this.userRepository.existsByUsernameAndDeletedIsFalse(updateUserRequest.getUsername())) {
                    throw new UserAlreadyExistsException("Username is taken");
                }
                Boolean updated = webClientBuilder.build().put()
                        .uri("http://authenticationauthorization-service/auth/update/{oldUsername}",
                                user.getUsername())
                        .bodyValue(updateUserRequest.getUsername())
                        .retrieve()
                        .onStatus(HttpStatusCode::isError, clientResponse -> {
                            if (clientResponse.statusCode() == HttpStatus.CONFLICT) {
                                throw new UserAlreadyExistsException("Username already exists in in authentication-authorization-service");
                            } else if (clientResponse.statusCode() == HttpStatus.NOT_FOUND) {
                                throw new UserNotFoundException("Username not found in authentication-authorization-service");
                            } else {
                                throw new UnexpectedException("Unexpected exception in authentication-authorization-service");
                            }
                        })
                        .bodyToMono(Boolean.class)
                        .block();
                if (updated != null && updated) {
                    user.setUsername(updateUserRequest.getUsername());
                } else {
                    throw new UnexpectedException("Unexpected exception failure to change username in authentication-authorization-service");
                }
            }
            if (updateUserRequest.getFirstName() != null && !user.getFirstName().equals(updateUserRequest.getFirstName())) {
                user.setFirstName(updateUserRequest.getFirstName());
            }
            if (updateUserRequest.getLastName() != null && !user.getLastName().equals(updateUserRequest.getLastName())) {
                user.setLastName(updateUserRequest.getLastName());
            }
            if (updateUserRequest.getGender() != null && !user.getGender().equals(updateUserRequest.getGender())) {
                user.setGender(updateUserRequest.getGender());
            }
            User saved = this.userRepository.save(user);
            return this.modelMapperService.forResponse().map(saved, GetAllUsersResponse.class);
        } else {
            throw new UserNotFoundException("User not found! ID: " + updateUserRequest.getId());
        }
    }

    @Override
    public GetAllUsersResponse deleteUser(Long id) {
        Optional<User> optionalUser = this.userRepository.findById(id);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            Boolean deleteFromAuth = this.webClientBuilder.build().put()
                    .uri("http://authenticationauthorization-service/auth/delete")
                    .bodyValue(user.getUsername())
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, clientResponse -> {
                        if (clientResponse.statusCode() == HttpStatus.NOT_FOUND) {
                            throw new UserNotFoundException("User not found in authentication-authorization-service");
                        } else {
                            throw new UnexpectedException("Unexpected exception in authentication-authorization-service");
                        }
                    })
                    .bodyToMono(Boolean.class)
                    .block();
            if (deleteFromAuth != null && deleteFromAuth) {
                user.setDeleted(true);
                User saved = this.userRepository.save(user);
                return this.modelMapperService.forResponse().map(saved, GetAllUsersResponse.class);
            } else {
                throw new UnexpectedException("User not found in authentication-authorization-service! ID: " + id);
            }
        } else {
            throw new UserNotFoundException("User not found! ID: " + id);
        }
    }

    private Sort.Direction getSortDirection(String direction) {
        if (direction.equals("asc")) {
            return Sort.Direction.ASC;
        } else if (direction.equals("desc")) {
            return Sort.Direction.DESC;
        }
        return Sort.Direction.ASC;
    }

    private List<Sort.Order> getOrder(String[] sort) {
        List<Sort.Order> orders = new ArrayList<>();
        if (sort[0].contains(",")) {
            for (String sortOrder : sort) {
                String[] _sort = sortOrder.split(",");
                orders.add(new Sort.Order(getSortDirection(_sort[1]), _sort[0]));
            }
        } else {
            orders.add(new Sort.Order(getSortDirection(sort[1]), sort[0]));
        }
        return orders;
    }

    @Override
    public Page<GetAllUsersResponse> getAllUsersPage(int page, int size, String[] sort, Long id, String firstName,
                                                     String lastName, String userName, String email) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(getOrder(sort)));
        Page<User> pageUser = this.userRepository.getUsersFiltered(id, firstName, lastName, userName, email, pageable);
        return pageUser.map(user -> this.modelMapperService.forResponse()
                        .map(user, GetAllUsersResponse.class));
    }

    @Override
    public GetAllUsersResponse addRole(Long id, Role role) {
        Optional<User> optionalUser = this.userRepository.findById(id);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            if (user.getRoles().contains(role)) {
                throw new RoleAlreadyExistsException("User already has this Role. Role: " + role.toString());
            }
            Boolean success = this.webClientBuilder.build().put()
                    .uri("http://authenticationauthorization-service/auth/add-role/{username}",
                            user.getUsername())
                    .bodyValue(role.toString())
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, clientResponse -> {
                        HttpHeaders headers = clientResponse.headers().asHttpHeaders();
                        String exceptionType = headers.getFirst("exception-type");
                        if (clientResponse.statusCode() == HttpStatus.NOT_FOUND && Objects.equals(exceptionType, "RoleNotFound")) {
                            throw new RoleNotFoundException("Role not found in authentication-authorization-service");
                        } else if (clientResponse.statusCode() == HttpStatus.NOT_FOUND && Objects.equals(exceptionType, "UserNotFound")) {
                            throw new UserNotFoundException("User not found in authentication-authorization-service");
                        } else {
                            throw new UnexpectedException("Unexpected exception in authentication-authorization-service");
                        }
                    })
                    .bodyToMono(Boolean.class)
                    .block();
            if (success != null && success) {
                user.getRoles().add(role);
                this.userRepository.save(user);
                return this.modelMapperService.forResponse().map(user, GetAllUsersResponse.class);
            } else {
                throw new UnexpectedException("Failed to add role in authentication-authorization-service");
            }
        } else {
            throw new UserNotFoundException("User not found! ID: " + id);
        }
    }

    @Override
    public GetAllUsersResponse removeRole(Long id, Role role) {
        Optional<User> optionalUser = this.userRepository.findById(id);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.getRoles().size() <= 1) {
                throw new SingleRoleRemovalException("Cannot remove role. User must have at least one role");
            }
            if (!user.getRoles().contains(role)) {
                throw new RoleNotFoundException("The user does not own this role! Role: " + role);
            }
            Boolean success = this.webClientBuilder.build().put()
                    .uri("http://authenticationauthorization-service/auth/remove-role/{username}",
                            user.getUsername())
                    .bodyValue(role.toString())
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, clientResponse -> {
                        HttpHeaders headers = clientResponse.headers().asHttpHeaders();
                        String exceptionType = headers.getFirst("exception-type");
                        if (clientResponse.statusCode() == HttpStatus.NOT_FOUND && Objects.equals(exceptionType, "RoleNotFound")) {
                            throw new RoleNotFoundException("Role not found in authentication-authorization-service");
                        } else if (clientResponse.statusCode() == HttpStatus.NOT_FOUND && Objects.equals(exceptionType, "UserNotFound")) {
                            throw new UserNotFoundException("User not found in authentication-authorization-service");
                        } else {
                            throw new UnexpectedException("Unexpected exception in authentication-authorization-service");
                        }
                    })
                    .bodyToMono(Boolean.class)
                    .block();
            if (success != null && success) {
                user.getRoles().remove(role);
                this.userRepository.save(user);
                return this.modelMapperService.forResponse().map(user, GetAllUsersResponse.class);
            } else {
                throw new UnexpectedException("Remove role failed in authentication-authorization-service");
            }
        } else {
            throw new UserNotFoundException("User not found! ID: " + id);
        }
    }
}
