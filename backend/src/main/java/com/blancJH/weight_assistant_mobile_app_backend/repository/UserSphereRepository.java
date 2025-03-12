package com.blancJH.weight_assistant_mobile_app_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.blancJH.weight_assistant_mobile_app_backend.model.UserSphere;

public interface UserSphereRepository extends JpaRepository<UserSphere, Long> {

    // Find spheres owned by a user
    List<UserSphere> findByUserId(Long userId);

    // Find a specific sphere owned by a user
    UserSphere findByUserIdAndSphereId(Long userId, Long sphereId);
}
