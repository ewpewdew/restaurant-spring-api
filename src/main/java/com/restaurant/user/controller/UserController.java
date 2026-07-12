package com.restaurant.user.controller;

import com.restaurant.common.dto.ApiResponse;
import com.restaurant.user.dto.request.CreateUserRequest;
import com.restaurant.user.dto.response.CreateUserResponse;
import com.restaurant.user.entity.User;
import com.restaurant.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

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


    @GetMapping("/findUserById")
    ResponseEntity<ApiResponse<Optional<User>>> findUserById(
            @RequestParam Long id
    ) {

        Optional<User> userResponse = userService.findUserById(id);

        return ResponseEntity.ok().body(ApiResponse.ok("Успешно", userResponse));
    }

}
