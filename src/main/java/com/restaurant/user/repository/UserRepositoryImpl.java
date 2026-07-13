package com.restaurant.user.repository;

import com.restaurant.user.entity.User;
import com.restaurant.user.mapper.UserRowMapper;
import com.restaurant.user.sql.UserSql;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final JdbcTemplate jdbcTemplate;
    private final UserRowMapper userRowMapper;

    public UserRepositoryImpl(JdbcTemplate jdbcTemplate, UserRowMapper userRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.userRowMapper = userRowMapper;
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
        List<User> users = jdbcTemplate.query(
                UserSql.FIND_BY_ID,
                userRowMapper,
                id
        );
        return users.isEmpty() ? Optional.empty() : Optional.of(users.getFirst());
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

    @Override
    public void update(Long id, User user) {
        jdbcTemplate.update(
                UserSql.UPDATE_USER,
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhone(),
                id
        );
    }
}