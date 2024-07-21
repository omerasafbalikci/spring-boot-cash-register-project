package com.toyota.usermanagementservice.resource;

import com.toyota.usermanagementservice.domain.Gender;
import com.toyota.usermanagementservice.domain.Role;
import com.toyota.usermanagementservice.dto.requests.CreateUserRequest;
import com.toyota.usermanagementservice.dto.requests.UpdateUserRequest;
import com.toyota.usermanagementservice.dto.responses.GetAllUsersResponse;
import com.toyota.usermanagementservice.dto.responses.UserManagementResponse;
import com.toyota.usermanagementservice.service.abstracts.UserService;
import com.toyota.usermanagementservice.utilities.exceptions.UserNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {
    @Mock
    private UserService userService;
    @InjectMocks
    private UserController userController;

    @Test
    void getAllUsersPage() {
        // Given
        Long id = 1L;
        String firstname = "firstname";
        String lastname = "lastname";
        String username = "username";
        String email = "email";
        String gender = "gender";
        int page = 1;
        int size = 3;
        String[] sort = {"firstname", "asc"};
        Sort.Order sortOrder = new Sort.Order(Sort.Direction.ASC, sort[0]);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortOrder));
        List<GetAllUsersResponse> content = List.of(new GetAllUsersResponse());

        // When
        Page<GetAllUsersResponse> pageMock = new PageImpl<>(content, pageable, 1);
        when(userService.getAllUsersPage(page, size, sort, id, firstname, lastname, username
                , email, gender)).thenReturn(pageMock);
        Page<GetAllUsersResponse> pageResponse = userController.getAllUsersPage(page, size, sort, id, firstname,
                lastname, username, email, gender);

        // Then
        Mockito.verify(userService).getAllUsersPage(page, size, sort, id, firstname,
                lastname, username, email, gender);
        assertNotNull(pageResponse);
        assertEquals(content, pageResponse.getContent());
    }

    @Test
    void getUserByEmail_Success() {
        // Given
        String email = "test@example.com";
        UserManagementResponse expectedResponse = new UserManagementResponse("username", email);

        when(userService.getUserByEmail(email)).thenReturn(expectedResponse);

        // When
        UserManagementResponse actualResponse = userController.getUserByEmail(email);

        // Then
        assertEquals(expectedResponse.getEmail(), actualResponse.getEmail());
        assertEquals(expectedResponse.getUsername(), actualResponse.getUsername());
    }

    @Test
    void getUserByEmail_UserNotFound() {
        // Given
        String email = "nonexistent@example.com";

        when(userService.getUserByEmail(email)).thenThrow(new UserNotFoundException("User not found"));

        // When / Then
        assertThrows(UserNotFoundException.class, () -> userController.getUserByEmail(email));
    }

    @Test
    void createUser() {
        // Given
        CreateUserRequest createUserRequest = new CreateUserRequest("firstname", "lastname", "username",
                "email@gmail.com", "abcdef1", Set.of(Role.CASHIER), Gender.FEMALE);
        GetAllUsersResponse response = new GetAllUsersResponse(1L, "firstname", "lastname", "username",
                "email@gmail.com", Set.of(Role.CASHIER), Gender.FEMALE);

        // When
        when(userService.createUser(any(CreateUserRequest.class))).thenReturn(response);
        ResponseEntity<GetAllUsersResponse> result = userController.createUser(createUserRequest);

        // Then
        Mockito.verify(userService).createUser(any(CreateUserRequest.class));
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(createUserRequest.getUsername(), result.getBody().getUsername());
    }

    @Test
    void updateUser() {
        // Given
        UpdateUserRequest updateUserRequest = new UpdateUserRequest();
        GetAllUsersResponse response = new GetAllUsersResponse(1L, "updated", "updated", "updated",
                "updated@gmail.com", Set.of(Role.CASHIER), Gender.FEMALE);

        // When
        when(userService.updateUser(any(UpdateUserRequest.class))).thenReturn(response);
        ResponseEntity<GetAllUsersResponse> result = userController.updateUser(updateUserRequest);

        // Then
        Mockito.verify(userService).updateUser(any(UpdateUserRequest.class));
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(response.getUsername(), result.getBody().getUsername());
    }

    @Test
    void addRole() {
        // Given
        GetAllUsersResponse response = new GetAllUsersResponse();

        // When
        when(userService.addRole(Mockito.anyLong(), any())).thenReturn(response);
        ResponseEntity<GetAllUsersResponse> result = userController.addRole(1L, Role.ADMIN);

        // Then
        Mockito.verify(userService).addRole(Mockito.anyLong(), any());
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void removeRole() {
        // Given
        GetAllUsersResponse response = new GetAllUsersResponse();

        // When
        when(userService.removeRole(Mockito.anyLong(), any())).thenReturn(response);
        ResponseEntity<GetAllUsersResponse> result = userController.removeRole(1L, Role.ADMIN);

        // Then
        Mockito.verify(userService).removeRole(Mockito.anyLong(), any());
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void deleteUser() {
        // Given
        GetAllUsersResponse response = new GetAllUsersResponse();

        // When
        when(userService.deleteUser(Mockito.anyLong())).thenReturn(response);
        ResponseEntity<GetAllUsersResponse> result = userController.deleteUser(1L);

        // Then
        Mockito.verify(userService).deleteUser(Mockito.anyLong());
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }
}
