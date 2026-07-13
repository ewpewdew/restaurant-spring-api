package com.restaurant.user.repository;

import com.restaurant.user.entity.User;

import java.util.Optional;

public interface UserRepository {
    Long save(User user);
    Optional<User> findById(Long id);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
    void update(Long id, User user);
    void patch(Long id, String firstName, String lastName, String email, String phone, Boolean active);
}
