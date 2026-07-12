package com.restaurant.user.sql;

public final class UserSql {
    private UserSql() {
    }

    public static final String EXISTS_BY_EMAIL = """
            SELECT EXISTS (
                SELECT 1
                FROM users
                WHERE email = :email
            )
            """;

    public static final String INSERT_USER = """
           INSERT INTO users (
                first_name,
                last_name,
                email,
                phone,
                is_active
            )
            VALUES (
                :firstName,
                :lastName,
                :email,
                :phone,
                :isActive
            )
            RETURNING id;
           """;

    public static final String EXISTS_BY_PHONE = """
        SELECT EXISTS (
            SELECT 1
            FROM users
            WHERE phone = :phone
        )
        """;


    public static final String FIND_BY_ID = """
        SELECT
            id,
            first_name,
            last_name,
            email,
            phone,
            is_active,
            created_at,
            updated_at
        FROM users
        WHERE id = :id
        """;

}
