package com.toyota.usermanagementservice.resource;

import com.toyota.usermanagementservice.domain.Role;
import com.toyota.usermanagementservice.dto.requests.CreateUserRequest;
import com.toyota.usermanagementservice.dto.requests.UpdateUserRequest;
import com.toyota.usermanagementservice.dto.responses.GetAllUsersResponse;
import com.toyota.usermanagementservice.service.abstracts.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing user management.
 */

@RestController
@RequestMapping("/api/user-management")
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    /**
     * Retrieves a paginated list of all users with optional filtering and sorting.
     *
     * @param page the page number to retrieve
     * @param size the number of users per page
     * @param sort the sorting criteria
     * @param id optional user ID to filter by
     * @param firstName optional first name to filter by
     * @param lastName optional last name to filter by
     * @param username optional username to filter by
     * @param email optional email to filter by
     * @param gender optional gender to filter by
     * @return a page of users
     */
    @GetMapping()
    public Page<GetAllUsersResponse> getAllUsersPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size,
            @RequestParam(defaultValue = "id,asc") String[] sort,
            @RequestParam(defaultValue = "") Long id,
            @RequestParam(defaultValue = "") String firstName,
            @RequestParam(defaultValue = "") String lastName,
            @RequestParam(defaultValue = "") String username,
            @RequestParam(defaultValue = "") String email,
            @RequestParam(defaultValue = "") String gender
    ) {
        return this.userService.getAllUsersPage(page, size, sort, id, firstName, lastName, username, email, gender);
    }

    /**
     * Creates a new user.
     *
     * @param createUserRequest the request containing user details
     * @return the created user
     */
    @PostMapping("/create")
    public ResponseEntity<GetAllUsersResponse> createUser(@RequestBody @Valid CreateUserRequest createUserRequest) {
        GetAllUsersResponse response = this.userService.createUser(createUserRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Updates an existing user.
     *
     * @param updateUserRequest the request containing updated user details
     * @return the updated user
     */
    @PutMapping("/update")
    public ResponseEntity<GetAllUsersResponse> updateUser(@RequestBody UpdateUserRequest updateUserRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.updateUser(updateUserRequest));
    }

    /**
     * Adds a role to the user with the specified ID.
     *
     * @param id the ID of the user
     * @param role the role to be added
     * @return the updated user
     */
    @PutMapping("/role/add/{user_id}")
    public ResponseEntity<GetAllUsersResponse> addRole(@PathVariable("user_id") Long id, @RequestBody Role role) {
        GetAllUsersResponse response = this.userService.addRole(id, role);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * Removes a role from the user with the specified ID.
     *
     * @param id the ID of the user
     * @param role the role to be removed
     * @return the updated user
     */
    @PutMapping("/role/remove/{user_id}")
    public ResponseEntity<GetAllUsersResponse> removeRole(@PathVariable("user_id") Long id, @RequestBody Role role) {
        GetAllUsersResponse response = this.userService.removeRole(id, role);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * Deletes the user with the specified ID.
     *
     * @param id the ID of the user to be deleted
     * @return the deleted user
     */
    @PutMapping("/delete")
    public ResponseEntity<GetAllUsersResponse> deleteUser(@RequestParam Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.deleteUser(id));
    }
}
