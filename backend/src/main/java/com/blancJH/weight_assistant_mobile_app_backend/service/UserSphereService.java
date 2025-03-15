package com.blancJH.weight_assistant_mobile_app_backend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.blancJH.weight_assistant_mobile_app_backend.model.Sphere;
import com.blancJH.weight_assistant_mobile_app_backend.model.User;
import com.blancJH.weight_assistant_mobile_app_backend.model.UserSphere;
import com.blancJH.weight_assistant_mobile_app_backend.repository.SphereRepository;
import com.blancJH.weight_assistant_mobile_app_backend.repository.UserSphereRepository;

@Service
public class UserSphereService {

    private final UserSphereRepository userSphereRepository;
    private final SphereRepository sphereRepository;

    @Autowired
    public UserSphereService(UserSphereRepository userSphereRepository, SphereRepository sphereRepository) {
        this.userSphereRepository = userSphereRepository;
        this.sphereRepository = sphereRepository;
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
        Optional<UserSphere> optionalUserSphere = userSphereRepository.findByUserIdAndSphereId(user.getId(), sphere.getId());
        UserSphere userSphere;
        if (optionalUserSphere.isPresent()) {
            // If the user already owns the sphere, increase the quantity.
            userSphere = optionalUserSphere.get();
            userSphere.setQuantity(userSphere.getQuantity() + 1);
        } else {
            // Otherwise, create a new entry with level 1.
            userSphere = new UserSphere();
            userSphere.setUser(user);
            userSphere.setSphere(sphere);
            userSphere.setQuantity(1);
            userSphere.setLevel(1); // Ensure level always starts at 1.
            userSphere.setRepresentator(false);
        }
        userSphereRepository.save(userSphere);
    }

    /**
     * Give the default sphere "Rocky" to a user when they register.
     */
    public void giveDefaultSphereToUser(User user) {
        Sphere defaultSphere = sphereRepository.findBySphereName("Rocky")
            .orElseThrow(() -> new IllegalArgumentException("Default sphere 'Rocky' not found"));
        addSphereToUser(user, defaultSphere);

        // Retrieve the created UserSphere and mark it as the representator.
        UserSphere userSphere = userSphereRepository
            .findByUserIdAndSphereId(user.getId(), defaultSphere.getId())
            .orElseThrow(() -> new IllegalArgumentException("UserSphere not found for default sphere"));
        
        userSphere.setRepresentator(true);
        userSphereRepository.save(userSphere);
    }

    /**
     * Upgrade a sphere if the user has enough copies.
     */
    public boolean upgradeSphere(Long userId, Long sphereId) {
        UserSphere userSphere = userSphereRepository.findByUserIdAndSphereId(userId, sphereId)
                .orElseThrow(() -> new IllegalArgumentException("UserSphere not found"));

        if (userSphere == null || userSphere.getQuantity() < 5) {
            return false;  // Not enough spheres to upgrade
        }

        userSphere.setQuantity(userSphere.getQuantity() - 5);  // Use 5 copies for upgrade
        userSphere.setLevel(userSphere.getLevel() + 1);  // Increase level

        userSphereRepository.save(userSphere);
        return true;
    }

    /**
     * Update a sphere as a representator, ensuring only one can be true.
     */
    @Transactional
    public void markAsRepresentator(Long userSphereId) {
        UserSphere userSphere = userSphereRepository.findById(userSphereId)
                .orElseThrow(() -> new IllegalArgumentException("UserSphere not found"));

        // Reset the current representative sphere for the user
        userSphereRepository.findByUserIdAndRepresentatorTrue(userSphere.getUser().getId())
                .ifPresent(currentRepresentator -> {
                    currentRepresentator.setRepresentator(false);
                    userSphereRepository.save(currentRepresentator);
                });

        // Set the new representative sphere
        userSphere.setRepresentator(true);
        userSphereRepository.save(userSphere);
    }

    /**
     * Fetch the representator sphere for a user.
     */
    public Optional<UserSphere> getRepresentator(Long userId) {
        return userSphereRepository.findByUserIdAndRepresentatorTrue(userId);
    }

}
