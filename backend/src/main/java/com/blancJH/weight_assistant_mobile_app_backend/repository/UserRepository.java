package com.blancJH.weight_assistant_mobile_app_backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.blancJH.weight_assistant_mobile_app_backend.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
}
