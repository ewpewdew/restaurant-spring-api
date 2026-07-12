package com.restaurant.user.entity;

import java.time.LocalDateTime;

public class User {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // КОНСТРУКТОР ДЛЯ СОЗДАНИЯ НОВОГО ПОЛЬЗОВАТЕЛЯ
    public User(String firstName, String lastName, String email, String phone, Boolean isActive) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.active = isActive;
    }

    // Конструктор для чтения из БД
    public User(String firstName, String lastName, String email, String phone,
                Boolean isActive, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.active = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Пустой конструктор (нужен для Spring)
    public User() {
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}