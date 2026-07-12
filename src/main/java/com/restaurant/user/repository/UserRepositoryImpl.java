package com.restaurant.user.repository;

import com.restaurant.user.entity.User;
import com.restaurant.user.mapper.UserRowMapper;
import com.restaurant.user.sql.UserSql;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final UserRowMapper userRowMapper;

    public UserRepositoryImpl(NamedParameterJdbcTemplate jdbcTemplate, UserRowMapper userRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.userRowMapper = userRowMapper;
    }

    @Override
    public Long save(User user) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("firstName", user.getFirstName())
                .addValue("lastName", user.getLastName())
                .addValue("email", user.getEmail())
                .addValue("phone", user.getPhone())
                .addValue("isActive", user.getActive());
        return jdbcTemplate.queryForObject(
                UserSql.INSERT_USER,
                params,
                Long.class
        );
    }

    @Override
    public Optional<User> findById(Long id) {

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id);

        List<User> users = jdbcTemplate.query(
                UserSql.FIND_BY_ID,
                params,
                userRowMapper
        );

        return users.stream().findFirst();
    }

    @Override
    public List<User> findAll() {
        return List.of();
    }

    @Override
    public boolean existsByEmail(String email) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("email", email);
        Boolean exists = jdbcTemplate.queryForObject(UserSql.EXISTS_BY_EMAIL, params, Boolean.class);
        return Boolean.TRUE.equals(exists);
    }

    @Override
    public boolean existsByPhone(String phone) {

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("phone", phone);

        Boolean exists = jdbcTemplate.queryForObject(
                UserSql.EXISTS_BY_PHONE,
                params,
                Boolean.class
        );

        return Boolean.TRUE.equals(exists);
    }
}