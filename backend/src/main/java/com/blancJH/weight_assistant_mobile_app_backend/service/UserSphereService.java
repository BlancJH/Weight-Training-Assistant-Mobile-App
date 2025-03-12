package com.blancJH.weight_assistant_mobile_app_backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.blancJH.weight_assistant_mobile_app_backend.model.Sphere;
import com.blancJH.weight_assistant_mobile_app_backend.model.User;
import com.blancJH.weight_assistant_mobile_app_backend.model.UserSphere;
import com.blancJH.weight_assistant_mobile_app_backend.repository.UserSphereRepository;

@Service
public class UserSphereService {

    private final UserSphereRepository userSphereRepository;

    @Autowired
    public UserSphereService(UserSphereRepository userSphereRepository) {
        this.userSphereRepository = userSphereRepository;
    }

    /**
     * Get all spheres a user owns.
     */
    public List<UserSphere> getUserSpheres(Long userId) {
        return userSphereRepository.findByUserId(userId);
    }

    /**
     * Add a sphere to the user's collection or increase quantity if they already own it.
     */
    public void addSphereToUser(User user, Sphere sphere) {
        UserSphere userSphere = userSphereRepository.findByUserIdAndSphereId(user.getId(), sphere.getId());

        if (userSphere == null) {
            // If the user does not have this sphere, create a new entry with level 1
            userSphere = new UserSphere();
            userSphere.setUser(user);
            userSphere.setSphere(sphere);
            userSphere.setQuantity(1);
            userSphere.setLevel(1); // Ensure level always starts at 1
        } else {
            // If the user already owns the sphere, increase quantity
            userSphere.setQuantity(userSphere.getQuantity() + 1);
        }

        userSphereRepository.save(userSphere);
    }

    /**
     * Upgrade a sphere if the user has enough copies.
     */
    public boolean upgradeSphere(Long userId, Long sphereId) {
        UserSphere userSphere = userSphereRepository.findByUserIdAndSphereId(userId, sphereId);

        if (userSphere == null || userSphere.getQuantity() < 5) {
            return false;  // Not enough spheres to upgrade
        }

        userSphere.setQuantity(userSphere.getQuantity() - 5);  // Use 5 copies for upgrade
        userSphere.setLevel(userSphere.getLevel() + 1);  // Increase level

        userSphereRepository.save(userSphere);
        return true;
    }

}
