package com.blancJH.weight_assistant_mobile_app_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.blancJH.weight_assistant_mobile_app_backend.model.Sphere;

public interface SphereRepository extends JpaRepository<Sphere, Long> {

    @Query(value = "SELECT * FROM spheres WHERE sphere_rank = ?1 ORDER BY RANDOM() LIMIT 1", nativeQuery = true)
    Sphere findRandomByRank(String rank);
}
