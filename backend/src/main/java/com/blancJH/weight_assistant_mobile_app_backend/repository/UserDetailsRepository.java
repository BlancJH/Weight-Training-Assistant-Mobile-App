package com.blancJH.weight_assistant_mobile_app_backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.blancJH.weight_assistant_mobile_app_backend.model.User;
import com.blancJH.weight_assistant_mobile_app_backend.model.UserDetails;

@Repository
public interface UserDetailsRepository extends JpaRepository<UserDetails, Long> {
    
    UserDetails findByUser(User user);

    Optional<UserDetails> findByUserId(Long userId);
}
