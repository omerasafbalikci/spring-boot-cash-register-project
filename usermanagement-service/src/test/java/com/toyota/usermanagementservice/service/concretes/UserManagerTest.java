package com.toyota.usermanagementservice.service.concretes;

import com.toyota.usermanagementservice.dao.UserRepository;
import com.toyota.usermanagementservice.domain.Gender;
import com.toyota.usermanagementservice.domain.Role;
import com.toyota.usermanagementservice.domain.User;
import com.toyota.usermanagementservice.dto.requests.CreateUserRequest;
import com.toyota.usermanagementservice.dto.requests.UpdateUserRequest;
import com.toyota.usermanagementservice.dto.responses.GetAllUsersResponse;
import com.toyota.usermanagementservice.utilities.exceptions.*;
import com.toyota.usermanagementservice.utilities.mappers.ModelMapperService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserManagerTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private WebClient.Builder webClientBuilder;
    @Mock
    private WebClient.ResponseSpec responseSpec;
    @Mock
    private WebClient webClient;
    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;
    @Mock
    private WebClient.RequestHeadersSpec<?> requestHeadersSpec;
    @Mock
    private WebClient.RequestBodySpec requestBodySpec;
    @Mock
    private ModelMapperService modelMapperService;
    @Mock
    private ModelMapper modelMapper;
    private UserManager userManager;

    @BeforeEach
    void setUp() {
        modelMapperService = mock(ModelMapperService.class);
        userManager = new UserManager(userRepository, webClientBuilder, modelMapperService);
    }

    @Test
    void createUser_success() {
        // Given
        CreateUserRequest createUserRequest = new CreateUserRequest("firstname", "lastname", "username",
                "email@gmail.com", "abcdef1", Set.of(Role.CASHIER), Gender.FEMALE);

        User user = new User();
        user.setId(1L);
        user.setFirstName("firstname");
        user.setLastName("lastname");
        user.setUsername("username");
        user.setEmail("email@gmail.com");
        user.setRoles(Set.of(Role.CASHIER));
        user.setGender(Gender.FEMALE);

        GetAllUsersResponse getAllUsersResponse = new GetAllUsersResponse(1L, "firstname", "lastname", "username",
                "email@gmail.com", false, Set.of(Role.CASHIER), Gender.FEMALE);

        // When
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(Mockito.anyString())).thenReturn(requestBodyUriSpec);
        doReturn(requestHeadersSpec).when(requestBodyUriSpec).bodyValue(any());
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Boolean.class)).thenReturn(Mono.just(true));

        when(userRepository.existsByUsernameAndDeletedIsFalse(anyString())).thenReturn(false);
        when(userRepository.existsByEmailAndDeletedIsFalse(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(1L);
            return savedUser;
        });

        when(modelMapperService.forRequest()).thenReturn(modelMapper);
        when(modelMapperService.forResponse()).thenReturn(modelMapper);
        when(modelMapper.map(any(CreateUserRequest.class), eq(User.class))).thenReturn(user);
        when(modelMapper.map(any(User.class), eq(GetAllUsersResponse.class))).thenReturn(getAllUsersResponse);

        GetAllUsersResponse response = userManager.createUser(createUserRequest);

        // Then
        Mockito.verify(userRepository).save(any(User.class));
        assertEquals(createUserRequest.getFirstName(), response.getFirstName());
        assertEquals(createUserRequest.getLastName(), response.getLastName());
        assertEquals(createUserRequest.getEmail(), response.getEmail());
        assertEquals(createUserRequest.getUsername(), response.getUsername());
        assertEquals(createUserRequest.getRoles(), response.getRoles());
        assertEquals(createUserRequest.getGender(), response.getGender());
    }

    @Test
    void createUser_fail() {
        // Given
        CreateUserRequest createUserRequest = new CreateUserRequest("firstname", "lastname", "username",
                "email@gmail.com", "abcdef1", Set.of(Role.CASHIER), Gender.FEMALE);

        ModelMapper modelMapper = mock(ModelMapper.class);

        when(modelMapperService.forRequest()).thenReturn(modelMapper);

        // When
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(Mockito.anyString())).thenReturn(requestBodyUriSpec);
        doReturn(requestHeadersSpec).when(requestBodyUriSpec).bodyValue(any());
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Boolean.class)).thenReturn(Mono.just(false));

        when(userRepository.existsByUsernameAndDeletedIsFalse(anyString())).thenReturn(false);
        when(userRepository.existsByEmailAndDeletedIsFalse(anyString())).thenReturn(false);

        // Then
        assertThrows(UnexpectedException.class,
                () -> userManager.createUser(createUserRequest));
    }

    @Test
    void createUser_usernameAlreadyExists() {
        // Given
        CreateUserRequest createUserRequest = new CreateUserRequest("firstname", "lastname", "username",
                "email@gmail.com", "abcdef1", Set.of(Role.CASHIER), Gender.FEMALE);

        // When
        when(userRepository.existsByUsernameAndDeletedIsFalse(anyString())).thenReturn(true);

        // Then
        assertThrows(UserAlreadyExistsException.class,
                () -> userManager.createUser(createUserRequest));
    }

    @Test
    void createUser_emailAlreadyExists() {
        // Given
        CreateUserRequest createUserRequest = new CreateUserRequest("firstname", "lastname", "username",
                "email@gmail.com", "abcdef1", Set.of(Role.CASHIER), Gender.FEMALE);

        // When
        when(userRepository.existsByEmailAndDeletedIsFalse(anyString())).thenReturn(true);

        // Then
        assertThrows(UserAlreadyExistsException.class,
                () -> userManager.createUser(createUserRequest));
    }

    @Test
    void updateUser_success() {
        // Given
        UpdateUserRequest updateUserRequest = new UpdateUserRequest(1L, "firstname", "lastname", "username",
                "email@gmail.com", Gender.FEMALE);
        User existingUser = new User(1L, "test", "test", "test",
                "test@gmail.com", Set.of(Role.CASHIER), Gender.MALE, false);

        // When
        // WebClient mock
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.put()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString(), any(Object[].class))).thenReturn(requestBodyUriSpec);
        doReturn(requestHeadersSpec).when(requestBodyUriSpec).bodyValue(anyString());
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        Mono<Boolean> mono = Mono.just(true);
        Mono<Boolean> monoSpy = Mockito.spy(mono);
        when(responseSpec.bodyToMono(Boolean.class)).thenReturn(monoSpy);
        // Repository mock
        when(userRepository.save(any(User.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(existingUser));

        when(modelMapperService.forResponse()).thenReturn(modelMapper);

        // ModelMapper mock
        GetAllUsersResponse getAllUsersResponse = new GetAllUsersResponse(1L, "firstname", "lastname", "username",
                "email@gmail.com", false, Set.of(Role.CASHIER), Gender.FEMALE);
        when(modelMapper.map(any(User.class), eq(GetAllUsersResponse.class))).thenReturn(getAllUsersResponse);

        GetAllUsersResponse response = userManager.updateUser(updateUserRequest);

        // Then
        Mockito.verify(userRepository).save(any(User.class));
        assertNotNull(response);
        assertEquals(updateUserRequest.getFirstName(), response.getFirstName());
        assertEquals(updateUserRequest.getLastName(), response.getLastName());
        assertEquals(updateUserRequest.getEmail(), response.getEmail());
        assertEquals(updateUserRequest.getUsername(), response.getUsername());
        assertEquals(updateUserRequest.getGender(), response.getGender());
    }

    @Test
    void updateUser_unexpectedFail() {
        // Given
        UpdateUserRequest updateUserRequest = new UpdateUserRequest(1L, "firstname", "lastname", "username",
                "email@gmail.com", Gender.FEMALE);
        User existingUser = new User(1L, "test", "test", "test",
                "test@gmail.com", Set.of(Role.CASHIER), Gender.MALE, false);

        // When
        // WebClient mock
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.put()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString(), (Object) any())).thenReturn(requestBodyUriSpec);
        doReturn(requestHeadersSpec).when(requestBodyUriSpec).bodyValue(anyString());
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        Mono<Boolean> mono = Mono.just(false);
        Mono<Boolean> monoSpy = Mockito.spy(mono);
        when(responseSpec.bodyToMono(Boolean.class)).thenReturn(monoSpy);
        // Repository mock
        when(userRepository.findById(any())).thenReturn(Optional.of(existingUser));

        // Then
        assertThrows(UnexpectedException.class, () -> userManager.updateUser(updateUserRequest));
    }

    @Test
    void updateUser_userNotFound() {
        // Given
        UpdateUserRequest updateUserRequest = new UpdateUserRequest();

        // When
        when(userRepository.findById(any())).thenReturn(Optional.empty());

        // Then
        assertThrows(UserNotFoundException.class, () -> userManager.updateUser(updateUserRequest));
    }

    @Test
    void updateUser_usernameTaken() {
        // Given
        UpdateUserRequest updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setUsername("Username");
        User user = new User();
        user.setUsername("oldUsername");

        // When
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(userRepository.existsByUsernameAndDeletedIsFalse(anyString())).thenReturn(true);

        // Then
        assertThrows(UserAlreadyExistsException.class, () -> userManager.updateUser(updateUserRequest));
    }

    @Test
    void updateUser_emailTaken() {
        // Given
        UpdateUserRequest updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setEmail("email");
        User user = new User();
        user.setEmail("oldEmail");

        // When
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(userRepository.existsByEmailAndDeletedIsFalse(anyString())).thenReturn(true);

        // Then
        assertThrows(UserAlreadyExistsException.class, () -> userManager.updateUser(updateUserRequest));
    }

    @Test
    void deleteUser_success() {
        // Given
        User existingUser = new User(1L, "test", "test", "test",
                "test@gmail.com", Set.of(Role.CASHIER), Gender.MALE, false);

        GetAllUsersResponse getAllUsersResponse = new GetAllUsersResponse(1L, "test", "test", "test",
                "test@gmail.com", true, Set.of(Role.CASHIER), Gender.MALE);

        // When
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.put()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodyUriSpec);
        doReturn(requestHeadersSpec).when(requestBodyUriSpec).bodyValue(anyString());
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        Mono<Boolean> mono = Mono.just(true);
        Mono<Boolean> monoSpy = Mockito.spy(mono);
        when(responseSpec.bodyToMono(Boolean.class)).thenReturn(monoSpy);
        when(userRepository.findById(any())).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
        when(modelMapper.map(any(User.class), eq(GetAllUsersResponse.class))).thenReturn(getAllUsersResponse);

        when(modelMapperService.forResponse()).thenReturn(modelMapper);

        GetAllUsersResponse response = userManager.deleteUser(1L);

        // Then
        Mockito.verify(userRepository).save(any(User.class));
        assertTrue(existingUser.isDeleted());
        assertNotNull(response);
        assertEquals(existingUser.getUsername(), response.getUsername());
    }

    @Test
    void deleteUser_userNotFound() {
        // Given
        Long userId = 1L;

        // When
        // Repository mock
        when(userRepository.findById(any())).thenReturn(Optional.empty());

        // Then
        assertThrows(UserNotFoundException.class,
                () -> userManager.deleteUser(userId));
    }

    @Test
    void deleteUser_unexpectedFail() {
        // Given
        User existingUser = new User(1L, "test", "test", "test",
                "test@gmail.com", Set.of(Role.CASHIER), Gender.MALE, false);
        Long userId = 1L;

        // When
        // WebClient mock
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.put()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        doReturn(requestHeadersSpec).when(requestBodySpec).bodyValue(anyString());
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        Mono<Boolean> mono = Mono.just(false);
        Mono<Boolean> monoSpy = Mockito.spy(mono);
        when(responseSpec.bodyToMono(Boolean.class)).thenReturn(monoSpy);
        // Repository mock
        when(userRepository.findById(any())).thenReturn(Optional.of(existingUser));

        // Then
        assertThrows(UnexpectedException.class, () -> userManager.deleteUser(userId));
    }

    @Test
    void getAllUsersPage_asc() {
        // Given
        Long id = 1L;
        String firstname = "firstname";
        String lastname = "lastname";
        String username = "username";
        String email = "email";
        String gender = "FEMALE";
        boolean deleted = false;
        Set<Role> roles = Set.of(Role.CASHIER);
        int page = 1;
        int size = 3;
        String[] sort = {"firstname", "asc"};
        Sort.Order sortOrder = new Sort.Order(Sort.Direction.ASC, sort[0]);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortOrder));
        List<User> content = List.of(new User(id, firstname, lastname, username, email, roles, Gender.valueOf(gender.toUpperCase()), deleted));

        // When
        Page<User> pageMock = new PageImpl<>(content, pageable, 1);
        doReturn(pageMock).when(userRepository).findAll(ArgumentMatchers.<Specification<User>>any(), any(Pageable.class));
        when(modelMapper.map(any(User.class), eq(GetAllUsersResponse.class)))
                .thenReturn(new GetAllUsersResponse(id, firstname, lastname, username, email, deleted, roles, Gender.valueOf(gender.toUpperCase())));

        when(modelMapperService.forResponse()).thenReturn(modelMapper);

        Page<GetAllUsersResponse> response = userManager.getAllUsersPage(page, size, sort, id, firstname, lastname, username, email, gender);

        // Then
        assertEquals(GetAllUsersResponse.class, response.getContent().get(0).getClass());
        assertEquals(page, response.getPageable().getPageNumber());
        assertEquals(size, response.getPageable().getPageSize());
        assertEquals(id, response.getContent().get(0).getId());
        assertEquals(firstname, response.getContent().get(0).getFirstName());
        assertEquals(lastname, response.getContent().get(0).getLastName());
        assertEquals(username, response.getContent().get(0).getUsername());
        assertEquals(email, response.getContent().get(0).getEmail());
        assertEquals(deleted, response.getContent().get(0).isDeleted());
        assertEquals(roles, response.getContent().get(0).getRoles());
        assertEquals(Gender.valueOf(gender.toUpperCase()), response.getContent().get(0).getGender());
    }

    @Test
    void getAllUsersPage_desc() {
        // Given
        Long id = 1L;
        String firstname = "firstname";
        String lastname = "lastname";
        String username = "username";
        String email = "email";
        String gender = "FEMALE";
        boolean deleted = false;
        Set<Role> roles = Set.of(Role.CASHIER);
        int page = 1;
        int size = 3;
        String[] sort = {"firstname", "desc"};
        Sort.Order sortOrder = new Sort.Order(Sort.Direction.DESC, sort[0]);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortOrder));
        List<User> content = List.of(new User(id, firstname, lastname, username, email, roles, Gender.valueOf(gender.toUpperCase()), deleted));

        // When
        Page<User> pageMock = new PageImpl<>(content, pageable, 1);
        doReturn(pageMock).when(userRepository).findAll(ArgumentMatchers.<Specification<User>>any(), any(Pageable.class));
        when(modelMapper.map(any(User.class), eq(GetAllUsersResponse.class)))
                .thenReturn(new GetAllUsersResponse(id, firstname, lastname, username, email, deleted, roles, Gender.valueOf(gender.toUpperCase())));

        when(modelMapperService.forResponse()).thenReturn(modelMapper);

        Page<GetAllUsersResponse> response = userManager.getAllUsersPage(page, size, sort, id, firstname, lastname, username, email, gender);

        // Then
        assertEquals(GetAllUsersResponse.class, response.getContent().get(0).getClass());
        assertEquals(page, response.getPageable().getPageNumber());
        assertEquals(size, response.getPageable().getPageSize());
        assertEquals(id, response.getContent().get(0).getId());
        assertEquals(firstname, response.getContent().get(0).getFirstName());
        assertEquals(lastname, response.getContent().get(0).getLastName());
        assertEquals(username, response.getContent().get(0).getUsername());
        assertEquals(email, response.getContent().get(0).getEmail());
        assertEquals(deleted, response.getContent().get(0).isDeleted());
        assertEquals(roles, response.getContent().get(0).getRoles());
        assertEquals(Gender.valueOf(gender.toUpperCase()), response.getContent().get(0).getGender());
    }

    @Test
    void addRole_success() {
        // Given
        Set<Role> roles = new HashSet<>();
        roles.add(Role.CASHIER);
        User existingUser = new User(1L, "test", "test", "test",
                "test@gmail.com", roles, Gender.MALE, false);
        Long userId = 1L;

        // Mock GetAllUsersResponse
        GetAllUsersResponse mappedResponse = new GetAllUsersResponse(
                existingUser.getId(),
                existingUser.getFirstName(),
                existingUser.getLastName(),
                existingUser.getUsername(),
                existingUser.getEmail(),
                existingUser.isDeleted(),
                new HashSet<>(existingUser.getRoles()),
                existingUser.getGender()
        );
        mappedResponse.getRoles().add(Role.ADMIN);

        // When
        // WebClient mock
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.put()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString(), (Object) any())).thenReturn(requestBodySpec);
        doReturn(requestHeadersSpec).when(requestBodySpec).bodyValue(anyString());
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        Mono<Boolean> mono = Mono.just(true);
        Mono<Boolean> monoSpy = Mockito.spy(mono);
        when(responseSpec.bodyToMono(Boolean.class)).thenReturn(monoSpy);

        when(modelMapperService.forResponse()).thenReturn(modelMapper);

        // Repository mock
        when(userRepository.save(any(User.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
        when(userRepository.findById(any())).thenReturn(Optional.of(existingUser));
        when(modelMapperService.forResponse().map(any(User.class), eq(GetAllUsersResponse.class))).thenReturn(mappedResponse);
        GetAllUsersResponse response = userManager.addRole(userId, Role.ADMIN);

        // Then
        Mockito.verify(userRepository).save(any(User.class));
        assertEquals(2, response.getRoles().size());
        assertTrue(response.getRoles().contains(Role.ADMIN));
    }

    @Test
    void addRole_unexpectedFail() {
        // Given
        Set<Role> roles = new HashSet<>();
        roles.add(Role.CASHIER);
        User existingUser = new User(1L, "test", "test", "test",
                "test@gmail.com", roles, Gender.MALE, false);
        Long userId = 1L;

        // When
        // WebClient mock
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.put()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString(), (Object) any())).thenReturn(requestBodySpec);
        doReturn(requestHeadersSpec).when(requestBodySpec).bodyValue(anyString());
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        Mono<Boolean> mono = Mono.just(false);
        Mono<Boolean> monoSpy = Mockito.spy(mono);
        when(responseSpec.bodyToMono(Boolean.class)).thenReturn(monoSpy);
        // Repository mock
        when(userRepository.findById(any())).thenReturn(Optional.of(existingUser));

        // Then
        assertThrows(UnexpectedException.class,
                () -> userManager.addRole(userId, Role.ADMIN));
    }

    @Test
    void addRole_alreadyExists() {
        // Given
        Set<Role> roles = new HashSet<>();
        roles.add(Role.CASHIER);
        User existingUser = new User(1L, "test", "test", "test",
                "test@gmail.com", roles, Gender.MALE, false);
        Long userId = 1L;

        // When
        // Repository mock
        when(userRepository.findById(any())).thenReturn(Optional.of(existingUser));

        // Then
        assertThrows(RoleAlreadyExistsException.class,
                () -> userManager.addRole(userId, Role.CASHIER));
    }

    @Test
    void addRole_userNotFound() {
        // Given
        Long userId = 1L;

        // When
        // Repository mock
        when(userRepository.findById(any())).thenReturn(Optional.empty());

        // Then
        assertThrows(UserNotFoundException.class,
                () -> userManager.addRole(userId, Role.CASHIER));
    }

    @Test
    void removeRole_success() {
        // Given
        Set<Role> roles = new HashSet<>();
        roles.add(Role.CASHIER);
        roles.add(Role.MANAGER);
        User existingUser = new User(1L, "test", "test", "test",
                "test@gmail.com", roles, Gender.MALE, false);
        Long userId = 1L;

        // WebClient mock
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.put()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString(), (Object) any())).thenReturn(requestBodySpec);
        doReturn(requestHeadersSpec).when(requestBodySpec).bodyValue(anyString());
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        Mono<Boolean> mono = Mono.just(true);
        when(responseSpec.bodyToMono(Boolean.class)).thenReturn(mono);

        // Repository mock
        when(userRepository.save(any(User.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
        when(userRepository.findById(any())).thenReturn(Optional.of(existingUser));

        when(modelMapperService.forResponse()).thenReturn(modelMapper);
        // ModelMapper mock
        GetAllUsersResponse mockResponse = new GetAllUsersResponse();
        mockResponse.setId(existingUser.getId());
        mockResponse.setFirstName(existingUser.getFirstName());
        mockResponse.setLastName(existingUser.getLastName());
        mockResponse.setUsername(existingUser.getUsername());
        mockResponse.setEmail(existingUser.getEmail());
        mockResponse.setDeleted(existingUser.isDeleted());
        mockResponse.setGender(existingUser.getGender());
        Set<Role> responseRoles = new HashSet<>(existingUser.getRoles());
        responseRoles.remove(Role.CASHIER);
        mockResponse.setRoles(responseRoles);

        when(modelMapperService.forResponse().map(any(User.class), eq(GetAllUsersResponse.class))).thenReturn(mockResponse);

        GetAllUsersResponse response = userManager.removeRole(userId, Role.CASHIER);

        // Then
        assertNotNull(response, "Response should not be null");
        Mockito.verify(userRepository).save(any(User.class));
        assertEquals(1, response.getRoles().size());
        assertFalse(response.getRoles().contains(Role.CASHIER));
    }

    @Test
    void removeRole_unexpectedFail() {
        // Given
        Set<Role> roles = new HashSet<>();
        roles.add(Role.CASHIER);
        roles.add(Role.MANAGER);
        User existingUser = new User(1L, "test", "test", "test",
                "test@gmail.com", roles, Gender.MALE, false);
        Long userId = 1L;

        // When
        // WebClient mock
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.put()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString(), (Object) any())).thenReturn(requestBodySpec);
        doReturn(requestHeadersSpec).when(requestBodySpec).bodyValue(anyString());
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        Mono<Boolean> mono = Mono.just(false);
        Mono<Boolean> monoSpy = Mockito.spy(mono);
        when(responseSpec.bodyToMono(Boolean.class)).thenReturn(monoSpy);
        // Repository mock
        when(userRepository.findById(any())).thenReturn(Optional.of(existingUser));

        // Then
        assertThrows(UnexpectedException.class,
                () -> userManager.removeRole(userId, Role.CASHIER));
    }

    @Test
    void removeRole_singleRemovalFail() {
        // Given
        Set<Role> roles = new HashSet<>();
        roles.add(Role.CASHIER);
        User existingUser = new User(1L, "test", "test", "test",
                "test@gmail.com", roles, Gender.MALE, false);
        Long userId = 1L;

        // When
        when(userRepository.findById(any())).thenReturn(Optional.of(existingUser));

        // Then
        assertThrows(SingleRoleRemovalException.class,
                () -> userManager.removeRole(userId, Role.CASHIER));
    }

    @Test
    void removeRole_roleNotFound() {
        // Given
        Set<Role> roles = new HashSet<>();
        roles.add(Role.CASHIER);
        roles.add(Role.MANAGER);
        User existingUser = new User(1L, "test", "test", "test",
                "test@gmail.com", roles, Gender.MALE, false);
        Long userId = 1L;

        // When
        when(userRepository.findById(any())).thenReturn(Optional.of(existingUser));

        // Then
        assertThrows(RoleNotFoundException.class,
                () -> userManager.removeRole(userId, Role.ADMIN));
    }

    @Test
    void removeRole_userNotFound() {
        // Given
        Long userId = 1L;

        // When
        when(userRepository.findById(any())).thenReturn(Optional.empty());

        // Then
        assertThrows(UserNotFoundException.class,
                () -> userManager.removeRole(userId, Role.ADMIN));
    }
}
