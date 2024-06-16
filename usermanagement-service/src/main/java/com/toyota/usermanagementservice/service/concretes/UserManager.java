package com.toyota.usermanagementservice.service.concretes;

import com.toyota.usermanagementservice.dao.UserRepository;
import com.toyota.usermanagementservice.dao.UserSpecification;
import com.toyota.usermanagementservice.domain.Role;
import com.toyota.usermanagementservice.domain.User;
import com.toyota.usermanagementservice.dto.requests.CreateUserRequest;
import com.toyota.usermanagementservice.dto.requests.RegisterRequest;
import com.toyota.usermanagementservice.dto.requests.UpdateUserRequest;
import com.toyota.usermanagementservice.dto.responses.GetAllUsersResponse;
import com.toyota.usermanagementservice.service.abstracts.UserService;
import com.toyota.usermanagementservice.utilities.exceptions.*;
import com.toyota.usermanagementservice.utilities.mappers.ModelMapperService;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service implementation for managing users.
 */

@Service
@Transactional
@AllArgsConstructor
public class UserManager implements UserService {
    private final UserRepository userRepository;
    private final Logger logger = LogManager.getLogger(UserService.class);
    private final WebClient.Builder webClientBuilder;
    private final ModelMapperService modelMapperService;

    /**
     * Creates a new user.
     *
     * @param createUserRequest the request containing user details
     * @return the created user details
     * @throws UserAlreadyExistsException if the username or email is already taken
     * @throws UnexpectedException if there is an unexpected error during user creation
     */
    @Override
    public GetAllUsersResponse createUser(CreateUserRequest createUserRequest) {
        logger.info("Creating user with username: {} and email: {}.", createUserRequest.getUsername(), createUserRequest.getEmail());
        if (this.userRepository.existsByUsernameAndDeletedIsFalse(createUserRequest.getUsername())) {
            logger.warn("Username '{}' is already taken.", createUserRequest.getUsername());
            throw new UserAlreadyExistsException("Username '" + createUserRequest.getUsername() + "' is already taken");
        }
        if (this.userRepository.existsByEmailAndDeletedIsFalse(createUserRequest.getEmail())) {
            logger.warn("Email '{}' is already taken.", createUserRequest.getEmail());
            throw new UserAlreadyExistsException("Email '" + createUserRequest.getEmail() + "' is already taken");
        }
        User user = this.modelMapperService.forRequest().map(createUserRequest, User.class);
        Set<String> roles = createUserRequest.getRoles().stream().map(Enum::toString).collect(Collectors.toSet());

        logger.info("Sending request to authentication-authorization-service for user signup.");
        Boolean response = this.webClientBuilder.build().post()
                .uri("http://authentication-authorization-service/auth/signup")
                .bodyValue(new RegisterRequest(createUserRequest.getUsername(), createUserRequest.getPassword(), roles))
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse -> {
                    if (clientResponse.statusCode() == HttpStatus.CONFLICT) {
                        logger.error("User already exists in authentication-authorization-service.");
                        throw new UserAlreadyExistsException("User already exists in authentication-authorization-service");
                    } else if (clientResponse.statusCode() == HttpStatus.BAD_REQUEST) {
                        logger.error("Problem with roles in authentication-authorization-service.");
                        throw new RoleNotFoundException("Problem with roles in authentication-authorization-service");
                    } else {
                        logger.error("Unexpected exception in authentication-authorization-service with status code: {}.", clientResponse.statusCode());
                        throw new UnexpectedException("Unexpected exception in authentication-authorization-service");
                    }
                })
                .bodyToMono(Boolean.class)
                .block();
        if (response == null || !response) {
            logger.error("Failed to create user! Reason: Unexpected problem in authentication-authorization-service.");
            throw new UnexpectedException("Failed to create user! Reason: Unexpected problem in authentication-authorization-service");
        }
        User saved = this.userRepository.save(user);
        logger.info("User created successfully with id: {}", saved.getId());
        return this.modelMapperService.forResponse().map(saved, GetAllUsersResponse.class);
    }

    /**
     * Updates an existing user.
     *
     * @param updateUserRequest the request containing updated user details
     * @return the updated user details
     * @throws UserNotFoundException if the user does not exist
     * @throws UserAlreadyExistsException if the new username or email is already taken
     * @throws UnexpectedException if there is an unexpected error during user update
     */
    @Override
    public GetAllUsersResponse updateUser(UpdateUserRequest updateUserRequest) {
        logger.info("Updating user with ID: {}.", updateUserRequest.getId());
        Optional<User> optionalUser = this.userRepository.findById(updateUserRequest.getId());

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            logger.info("User found {}.", user);
            if (updateUserRequest.getEmail() != null && !user.getEmail().equals(updateUserRequest.getEmail())) {
                logger.info("Updating email to: {}.", updateUserRequest.getEmail());
                if (this.userRepository.existsByEmailAndDeletedIsFalse(updateUserRequest.getEmail())) {
                    logger.warn("Email '{}' is already taken.", updateUserRequest.getEmail());
                    throw new UserAlreadyExistsException("Email is already taken");
                }
                user.setEmail(updateUserRequest.getEmail());
            }
            if (updateUserRequest.getUsername() != null && !user.getUsername().equals(updateUserRequest.getUsername())) {
                logger.info("Updating username to: {}.", updateUserRequest.getUsername());
                if (this.userRepository.existsByUsernameAndDeletedIsFalse(updateUserRequest.getUsername())) {
                    logger.warn("Username '{}' is already taken.", updateUserRequest.getUsername());
                    throw new UserAlreadyExistsException("Username is taken");
                }
                Boolean updated = webClientBuilder.build().put()
                        .uri("http://authentication-authorization-service/auth/update/{old-username}",
                                user.getUsername())
                        .bodyValue(updateUserRequest.getUsername())
                        .retrieve()
                        .onStatus(HttpStatusCode::isError, clientResponse -> {
                            if (clientResponse.statusCode() == HttpStatus.CONFLICT) {
                                logger.error("Username already exists in authentication-authorization-service.");
                                throw new UserAlreadyExistsException("Username already exists in in authentication-authorization-service");
                            } else if (clientResponse.statusCode() == HttpStatus.NOT_FOUND) {
                                logger.error("Username not found in authentication-authorization-service.");
                                throw new UserNotFoundException("Username not found in authentication-authorization-service");
                            } else {
                                logger.error("Unexpected exception in authentication-authorization-service with status code: {}.", clientResponse.statusCode());
                                throw new UnexpectedException("Unexpected exception in authentication-authorization-service");
                            }
                        })
                        .bodyToMono(Boolean.class)
                        .block();
                if (updated != null && updated) {
                    user.setUsername(updateUserRequest.getUsername());
                } else {
                    logger.error("Failed to update username in authentication-authorization-service.");
                    throw new UnexpectedException("Unexpected exception failure to change username in authentication-authorization-service");
                }
            }
            if (updateUserRequest.getFirstName() != null && !user.getFirstName().equals(updateUserRequest.getFirstName())) {
                logger.info("Updating first name to: {}.", updateUserRequest.getFirstName());
                user.setFirstName(updateUserRequest.getFirstName());
            }
            if (updateUserRequest.getLastName() != null && !user.getLastName().equals(updateUserRequest.getLastName())) {
                logger.info("Updating last name to: {}.", updateUserRequest.getLastName());
                user.setLastName(updateUserRequest.getLastName());
            }
            if (updateUserRequest.getGender() != null && !user.getGender().equals(updateUserRequest.getGender())) {
                logger.info("Updating gender to: {}.", updateUserRequest.getGender());
                user.setGender(updateUserRequest.getGender());
            }
            User saved = this.userRepository.save(user);
            return this.modelMapperService.forResponse().map(saved, GetAllUsersResponse.class);
        } else {
            logger.error("User not found! ID: {}.", updateUserRequest.getId());
            throw new UserNotFoundException("User not found! ID: " + updateUserRequest.getId());
        }
    }

    /**
     * Deletes a user by marking them as deleted in the local repository and removing from authentication service.
     *
     * @param id the ID of the user to delete
     * @return the deleted user details
     * @throws UserNotFoundException if the user does not exist
     * @throws UnexpectedException if there is an unexpected error during user deletion
     */
    @Override
    public GetAllUsersResponse deleteUser(Long id) {
        logger.info("Attempting to delete user with ID: {}.", id);
        Optional<User> optionalUser = this.userRepository.findById(id);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            logger.info("User found: {}.", user);
            Boolean deleteFromAuth = this.webClientBuilder.build().put()
                    .uri("http://authentication-authorization-service/auth/delete")
                    .bodyValue(user.getUsername())
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, clientResponse -> {
                        if (clientResponse.statusCode() == HttpStatus.NOT_FOUND) {
                            logger.error("User not found in authentication-authorization-service.");
                            throw new UserNotFoundException("User not found in authentication-authorization-service");
                        } else {
                            logger.error("Unexpected exception in authentication-authorization-service with status code: {}.", clientResponse.statusCode());
                            throw new UnexpectedException("Unexpected exception in authentication-authorization-service");
                        }
                    })
                    .bodyToMono(Boolean.class)
                    .block();
            if (deleteFromAuth != null && deleteFromAuth) {
                logger.info("User deleted from authentication-authorization-service successfully.");
                user.setDeleted(true);
                User saved = this.userRepository.save(user);
                logger.info("User marked as deleted in local repository: {}.", saved);
                return this.modelMapperService.forResponse().map(saved, GetAllUsersResponse.class);
            } else {
                logger.error("Failed to delete user from authentication-authorization-service! ID: {}", id);
                throw new UnexpectedException("User not found in authentication-authorization-service! ID: " + id);
            }
        } else {
            logger.error("User not found! ID: {}", id);
            throw new UserNotFoundException("User not found! ID: " + id);
        }
    }

    /**
     * Determines the sort direction based on the given direction string.
     *
     * @param direction the sort direction string
     * @return the corresponding Sort.Direction
     */
    private Sort.Direction getSortDirection(String direction) {
        if (direction.equals("asc")) {
            return Sort.Direction.ASC;
        } else if (direction.equals("desc")) {
            return Sort.Direction.DESC;
        }
        return Sort.Direction.ASC;
    }

    /**
     * Constructs a list of Sort.Order objects based on the given sort criteria.
     *
     * @param sort the sort criteria
     * @return the list of Sort.Order objects
     */
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

    /**
     * Retrieves a paginated list of users with optional filtering and sorting.
     *
     * @param page the page number to retrieve
     * @param size the number of users per page
     * @param sort the sorting criteria
     * @param id the optional user ID filter
     * @param firstName the optional first name filter
     * @param lastName the optional last name filter
     * @param username the optional username filter
     * @param email the optional email filter
     * @param gender the optional gender filter
     * @return the paginated list of users
     */
    @Override
    public Page<GetAllUsersResponse> getAllUsersPage(int page, int size, String[] sort, Long id, String firstName,
                                                     String lastName, String username, String email, String gender) {
        logger.info("Fetching users page: {} with size: {} and sort: {}.", page, size, Arrays.toString(sort));
        Pageable pageable = PageRequest.of(page, size, Sort.by(getOrder(sort)));
        Specification<User> specification = UserSpecification.filterByCriteria(id, firstName, lastName, username, email, gender);
        Page<User> pageUser = this.userRepository.findAll(specification, pageable);

        List<GetAllUsersResponse> responses = pageUser.getContent().stream()
                .map(user -> this.modelMapperService.forResponse()
                        .map(user, GetAllUsersResponse.class)).toList();
        logger.debug("Mapped {} users to response objects.", responses.size());

        return pageUser.map(user -> this.modelMapperService.forResponse().map(user, GetAllUsersResponse.class));
    }

    /**
     * Adds a role to a user.
     *
     * @param id the ID of the user
     * @param role the role to add
     * @return the updated user
     * @throws UserNotFoundException if the user is not found
     * @throws RoleAlreadyExistsException if the user already has the role
     * @throws RoleNotFoundException if the role is not found in the authentication-authorization-service
     * @throws UnexpectedException if there is an error during the process
     */
    @Override
    public GetAllUsersResponse addRole(Long id, Role role) {
        logger.info("Attempting to add role: {} to user with ID: {}.", role, id);
        Optional<User> optionalUser = this.userRepository.findById(id);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            logger.info("User found: {}.", user);
            if (user.getRoles().contains(role)) {
                logger.warn("User already has this role: {}.", role);
                throw new RoleAlreadyExistsException("User already has this Role. Role: " + role.toString());
            }
            logger.info("Sending request to add role: {} to user: {}.", role, user.getUsername());
            Boolean success = this.webClientBuilder.build().put()
                    .uri("http://authentication-authorization-service/auth/add-role/{username}",
                            user.getUsername())
                    .bodyValue(role.toString())
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, clientResponse -> {
                        HttpHeaders headers = clientResponse.headers().asHttpHeaders();
                        String exceptionType = headers.getFirst("exception-type");
                        if (clientResponse.statusCode() == HttpStatus.NOT_FOUND && Objects.equals(exceptionType, "RoleNotFound")) {
                            logger.error("Role not found in authentication-authorization-service.");
                            throw new RoleNotFoundException("Role not found in authentication-authorization-service");
                        } else if (clientResponse.statusCode() == HttpStatus.NOT_FOUND && Objects.equals(exceptionType, "UserNotFound")) {
                            logger.error("User not found in authentication-authorization-service.");
                            throw new UserNotFoundException("User not found in authentication-authorization-service");
                        } else {
                            logger.error("Unexpected exception in authentication-authorization-service with status code: {}.", clientResponse.statusCode());
                            throw new UnexpectedException("Unexpected exception in authentication-authorization-service");
                        }
                    })
                    .bodyToMono(Boolean.class)
                    .block();
            if (success != null && success) {
                user.getRoles().add(role);
                this.userRepository.save(user);
                logger.info("Role: {} added to user: {}.", role, user);
                return this.modelMapperService.forResponse().map(user, GetAllUsersResponse.class);
            } else {
                logger.error("Failed to add role in authentication-authorization-service.");
                throw new UnexpectedException("Failed to add role in authentication-authorization-service");
            }
        } else {
            logger.error("User not found! ID: {}.", id);
            throw new UserNotFoundException("User not found! ID: " + id);
        }
    }

    /**
     * Removes a role from a user.
     *
     * @param id the ID of the user
     * @param role the role to remove
     * @return the updated user
     * @throws UserNotFoundException if the user is not found
     * @throws SingleRoleRemovalException if the user has only one role
     * @throws RoleNotFoundException if the user does not have the role or if the role is not found in the authentication-authorization-service
     * @throws UnexpectedException if there is an error during the process
     */
    @Override
    public GetAllUsersResponse removeRole(Long id, Role role) {
        logger.info("Attempting to remove role: {} from user with ID: {}.", role, id);
        Optional<User> optionalUser = this.userRepository.findById(id);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            logger.info("User found: {}.", user);
            if (user.getRoles().size() <= 1) {
                logger.warn("Cannot remove role: {}. User must have at least one role.", role);
                throw new SingleRoleRemovalException("Cannot remove role. User must have at least one role");
            }
            if (!user.getRoles().contains(role)) {
                logger.warn("The user does not own this role: {}.", role);
                throw new RoleNotFoundException("The user does not own this role! Role: " + role);
            }
            logger.info("Sending request to remove role: {} from user: {}.", role, user.getUsername());
            Boolean success = this.webClientBuilder.build().put()
                    .uri("http://authentication-authorization-service/auth/remove-role/{username}",
                            user.getUsername())
                    .bodyValue(role.toString())
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, clientResponse -> {
                        HttpHeaders headers = clientResponse.headers().asHttpHeaders();
                        String exceptionType = headers.getFirst("exception-type");
                        if (clientResponse.statusCode() == HttpStatus.NOT_FOUND && Objects.equals(exceptionType, "RoleNotFound")) {
                            logger.error("Role not found in authentication-authorization-service.");
                            throw new RoleNotFoundException("Role not found in authentication-authorization-service");
                        } else if (clientResponse.statusCode() == HttpStatus.NOT_FOUND && Objects.equals(exceptionType, "UserNotFound")) {
                            logger.error("User not found in authentication-authorization-service.");
                            throw new UserNotFoundException("User not found in authentication-authorization-service");
                        } else {
                            logger.error("Unexpected exception in authentication-authorization-service with status code: {}.", clientResponse.statusCode());
                            throw new UnexpectedException("Unexpected exception in authentication-authorization-service");
                        }
                    })
                    .bodyToMono(Boolean.class)
                    .block();
            if (success != null && success) {
                user.getRoles().remove(role);
                this.userRepository.save(user);
                logger.info("Role: {} removed from user: {}.", role, user);
                return this.modelMapperService.forResponse().map(user, GetAllUsersResponse.class);
            } else {
                logger.error("Failed to remove role in authentication-authorization-service.");
                throw new UnexpectedException("Remove role failed in authentication-authorization-service");
            }
        } else {
            logger.error("User not found! ID: {}.", id);
            throw new UserNotFoundException("User not found! ID: " + id);
        }
    }
}
