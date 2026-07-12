package com.restaurant.user.service;

import com.restaurant.user.dto.request.CreateUserRequest;
import com.restaurant.user.entity.User;

import java.util.Optional;

public interface UserService {

    Long createUser(CreateUserRequest request);
    Optional<User> findUserById(Long id);

}
