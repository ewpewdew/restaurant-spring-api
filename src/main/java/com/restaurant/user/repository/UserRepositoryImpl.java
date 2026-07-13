package com.restaurant.user.repository;

import com.restaurant.user.dto.request.PatchUserRequest;
import com.restaurant.user.entity.User;
import com.restaurant.user.mapper.UserRowMapper;
import com.restaurant.user.sql.UserSql;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final UserRowMapper userRowMapper;

    public UserRepositoryImpl(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate, UserRowMapper userRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
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

    @Override
    public void patch(Long id, String firstName, String lastName, String email, String phone, Boolean active) {
        StringBuilder sql = new StringBuilder("UPDATE users SET ");
        MapSqlParameterSource params = new MapSqlParameterSource("id", id);

        if (firstName != null) {
            sql.append("first_name = :firstName, ");
            params.addValue("firstName", firstName);
        }
        if (lastName != null) {
            sql.append("last_name = :lastName, ");
            params.addValue("lastName", lastName);
        }
        if (email != null) {
            sql.append("email = :email, ");
            params.addValue("email", email);
        }
        if (phone != null) {
            sql.append("phone = :phone, ");
            params.addValue("phone", phone);
        }
        if (active != null) {
            sql.append("is_active = :is_active, ");
            params.addValue("is_active", active);
        }

        sql.append("updated_at = NOW() WHERE id = :id");
        namedParameterJdbcTemplate.update(sql.toString(), params);
    }
}