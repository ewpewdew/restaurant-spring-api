package com.restaurant.user.repository;

import com.restaurant.user.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    Long save(User user);
    Optional<User> findById(Long id);
    List<User> findAll();
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
}
