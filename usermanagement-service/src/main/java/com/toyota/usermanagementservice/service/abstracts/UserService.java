package com.toyota.usermanagementservice.service.abstracts;

import com.toyota.usermanagementservice.domain.Role;
import com.toyota.usermanagementservice.dto.requests.CreateUserRequest;
import com.toyota.usermanagementservice.dto.requests.UpdateUserRequest;
import com.toyota.usermanagementservice.dto.responses.GetAllUsersResponse;
import org.springframework.data.domain.Page;

public interface UserService {
    GetAllUsersResponse createUser(CreateUserRequest createUserRequest);
    GetAllUsersResponse updateUser(UpdateUserRequest updateUserRequest);
    GetAllUsersResponse deleteUser(Long id);
    Page<GetAllUsersResponse> getAllUsersPage(int page, int size, String[] sort, Long id, String firstName,
                                              String lastName, String username, String email);
    GetAllUsersResponse addRole(Long id, Role role);
    GetAllUsersResponse removeRole(Long id, Role role);
}
