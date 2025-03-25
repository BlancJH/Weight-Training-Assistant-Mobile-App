package com.blancJH.weight_assistant_mobile_app_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.blancJH.weight_assistant_mobile_app_backend.model.Consent;

@Repository
public interface ConsentRepository extends JpaRepository<Consent, Long> {
    
}
