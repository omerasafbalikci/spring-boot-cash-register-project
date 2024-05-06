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

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/user-management")
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping()
    public Page<GetAllUsersResponse> getAllUsersPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size,
            @RequestParam(defaultValue = "id,asc") String[] sort,
            @RequestParam(defaultValue = "") Long id,
            @RequestParam(defaultValue = "") String firstName,
            @RequestParam(defaultValue = "") String lastName,
            @RequestParam(defaultValue = "") String username,
            @RequestParam(defaultValue = "") String email
    ) {
        return this.userService.getAllUsersPage(page, size, sort, id, firstName, lastName, username, email);
    }

    @PostMapping("/create")
    public ResponseEntity<GetAllUsersResponse> createUser(@RequestBody @Valid CreateUserRequest createUserRequest) {
        GetAllUsersResponse response = this.userService.createUser(createUserRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/update")
    public ResponseEntity<GetAllUsersResponse> updateUser(@RequestBody UpdateUserRequest updateUserRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.updateUser(updateUserRequest));
    }

    @PutMapping("/role/add/{user_id}")
    public ResponseEntity<GetAllUsersResponse> addRole(@PathVariable("user_id") Long id, @RequestBody Role role) {
        GetAllUsersResponse response = this.userService.addRole(id, role);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/role/remove/{user_id}")
    public ResponseEntity<GetAllUsersResponse> removeRole(@PathVariable("user_id") Long id, @RequestBody Role role) {
        GetAllUsersResponse response = this.userService.removeRole(id, role);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/delete")
    public ResponseEntity<GetAllUsersResponse> deleteUser(@RequestBody Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.deleteUser(id));
    }
}
