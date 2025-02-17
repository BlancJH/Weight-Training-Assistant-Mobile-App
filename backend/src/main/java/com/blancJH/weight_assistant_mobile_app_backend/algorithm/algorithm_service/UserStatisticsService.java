package com.blancJH.weight_assistant_mobile_app_backend.algorithm.algorithm_service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.blancJH.weight_assistant_mobile_app_backend.repository.UserRepository;

@Service
public class UserStatisticsService {

    @Autowired
    private UserRepository userRepository;

    public int getTotalUserCount() {
        // Assuming userRepository.count() returns a long.
        return (int) userRepository.count();
    }
}
