package com.restaurant.user.controller;

import com.restaurant.common.dto.ApiResponse;
import com.restaurant.user.dto.request.CreateUserRequest;
import com.restaurant.user.dto.request.UpdateUserRequest;
import com.restaurant.user.dto.response.CreateUserResponse;
import com.restaurant.user.dto.response.UserResponse;
import com.restaurant.user.entity.User;
import com.restaurant.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/create")
    ResponseEntity<ApiResponse<CreateUserResponse>> createUser(@Valid @RequestBody CreateUserRequest request) {
        Long userId = userService.createUser(request);
        CreateUserResponse response = new CreateUserResponse();
        response.setId(userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Пользователь успешно создан", response));
    }


    @GetMapping("/user/{id}")
    ResponseEntity<ApiResponse<UserResponse>> findUserById(@PathVariable Long id) {
        User user = userService.findUserById(id);
        return ResponseEntity.ok().body(ApiResponse.ok("Успешно", UserResponse.from(user)));
    }

    @PutMapping("/update/{id}")
    ResponseEntity<ApiResponse<CreateUserResponse>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request) {

        Long userId = userService.updateUser(id, request);
        CreateUserResponse response = new CreateUserResponse();
        response.setId(userId);

        return ResponseEntity.ok(ApiResponse.ok("Пользователь обновлён", response));
    }

}
