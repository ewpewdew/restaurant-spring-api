package com.restaurant.user.repository;

import com.restaurant.user.entity.User;
import com.restaurant.user.mapper.UserRowMapper;
import com.restaurant.user.sql.UserSql;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Long save(User user) {
        return jdbcTemplate.queryForObject(
                UserSql.INSERT_USER,
                Long.class,
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhone(),
                user.getActive()
        );
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(jdbcTemplate.queryForObject(
                UserSql.FIND_BY_ID,
                new BeanPropertyRowMapper<>(User.class),
                id
        ));
    }

    @Override
    public boolean existsByEmail(String email) {
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(
                UserSql.EXISTS_BY_EMAIL,
                Boolean.class,
                email
        ));
    }

    @Override
    public boolean existsByPhone(String phone) {
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(
                UserSql.EXISTS_BY_PHONE,
                Boolean.class,
                phone
        ));
    }
}