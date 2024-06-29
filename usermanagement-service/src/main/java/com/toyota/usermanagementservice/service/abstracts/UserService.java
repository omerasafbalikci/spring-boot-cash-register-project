package com.toyota.usermanagementservice.service.abstracts;

import com.toyota.usermanagementservice.domain.Role;
import com.toyota.usermanagementservice.dto.requests.CreateUserRequest;
import com.toyota.usermanagementservice.dto.requests.UpdateUserRequest;
import com.toyota.usermanagementservice.dto.responses.GetAllUsersResponse;
import com.toyota.usermanagementservice.dto.responses.UserManagementResponse;
import org.springframework.data.domain.Page;

/**
 * Interface for user's service class.
 */

public interface UserService {
    /**
     * Creates a new user.
     *
     * @param createUserRequest the request object containing user details
     * @return the added user details
     */
    GetAllUsersResponse createUser(CreateUserRequest createUserRequest);

    /**
     * Updates an existing user.
     *
     * @param updateUserRequest the request object containing updated user details
     * @return the updated user details
     */
    GetAllUsersResponse updateUser(UpdateUserRequest updateUserRequest);

    /**
     * Deletes a user by ID.
     *
     * @param id the ID of the user to be deleted
     * @return the deleted user details
     */
    GetAllUsersResponse deleteUser(Long id);

    /**
     * Retrieves a paginated list of users with optional filtering and sorting.
     *
     * @param page      the page number to retrieve
     * @param size      the size of the page to retrieve
     * @param sort      the sort parameters
     * @param id        the ID of the user to filter by
     * @param firstName the first name of the user to filter by
     * @param lastName  the last name of the user to filter by
     * @param username  the username of the user to filter by
     * @param email     the email of the user to filter by
     * @param gender    the gender of the user to filter by
     * @return a paginated response object containing details of all users
     */
    Page<GetAllUsersResponse> getAllUsersPage(int page, int size, String[] sort, Long id, String firstName,
                                              String lastName, String username, String email, String gender);

    /**
     * Retrieves user details by email.
     *
     * @param email the email of the user to retrieve
     * @return the user details
     */
    UserManagementResponse getUserByEmail(String email);

    /**
     * Adds a role to a user.
     *
     * @param id   the ID of the user
     * @param role the role to be added
     * @return the user to whom the role is added
     */
    GetAllUsersResponse addRole(Long id, Role role);

    /**
     * Removes a role from a user.
     *
     * @param id   the ID of the user
     * @param role the role to be removed
     * @return the user from whom the role has been removed
     */
    GetAllUsersResponse removeRole(Long id, Role role);
}
