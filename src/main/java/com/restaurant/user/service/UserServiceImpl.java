package com.restaurant.user.service;

import com.restaurant.common.enums.ErrorCode;
import com.restaurant.common.exception.ApiException;
import com.restaurant.user.dto.request.CreateUserRequest;
import com.restaurant.user.dto.request.UpdateUserRequest;
import com.restaurant.user.entity.User;
import com.restaurant.user.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(
            UserRepository userRepository
    ) {
        this.userRepository = userRepository;
    }

    @Override
    public Long createUser(CreateUserRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ApiException(
                    ErrorCode.EMAIL_ALREADY_EXISTS,
                    "Такой email уже существует"
            );
        }

        if (userRepository.existsByPhone(request.getPhone())) {
            throw new ApiException(
                    ErrorCode.PHONE_ALREADY_EXISTS,
                    "Такой телефон уже существует"
            );
        }

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setActive(true);

        return userRepository.save(user);

    }


    @Override
    public User findUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new ApiException(
                ErrorCode.NOT_FOUND,
                "Такой пользователь не найден"));
    }

    @Override
    public Long updateUser(Long id, UpdateUserRequest request) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND, "Пользователь не найден"));

        if (!existing.getEmail().equals(request.getEmail())
                && userRepository.existsByEmail(request.getEmail())) {
            throw new ApiException(ErrorCode.EMAIL_ALREADY_EXISTS, "Email уже занят");
        }

        if (!existing.getPhone().equals(request.getPhone())
                && userRepository.existsByPhone(request.getPhone())) {
            throw new ApiException(ErrorCode.PHONE_ALREADY_EXISTS, "Телефон уже занят");
        }

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setActive(existing.getActive());

        userRepository.update(id, user);

        return id;
    }

}
