package com.restaurant.user.service;

import com.restaurant.user.dto.request.CreateUserRequest;
import com.restaurant.user.entity.User;

public interface UserService {

    Long createUser(CreateUserRequest request);
    User findUserById(Long id);

}
