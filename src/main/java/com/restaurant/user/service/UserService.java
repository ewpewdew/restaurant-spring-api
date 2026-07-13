package com.restaurant.user.service;

import com.restaurant.user.dto.request.CreateUserRequest;
import com.restaurant.user.dto.request.PatchUserRequest;
import com.restaurant.user.dto.request.UpdateUserRequest;
import com.restaurant.user.entity.User;

public interface UserService {

    Long createUser(CreateUserRequest request);
    User findUserById(Long id);
    Long updateUser(Long id, UpdateUserRequest request);
    Long patchUser(Long id, PatchUserRequest request);
}
