package com.blancJH.weight_assistant_mobile_app_backend.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.blancJH.weight_assistant_mobile_app_backend.model.User;
import com.blancJH.weight_assistant_mobile_app_backend.model.UserDetails;
import com.blancJH.weight_assistant_mobile_app_backend.repository.UserDetailsRepository;

@Service
public class UserDetailsService {

    private final UserDetailsRepository userDetailsRepository;

    public UserDetailsService(UserDetailsRepository userDetailsRepository) {
        this.userDetailsRepository = userDetailsRepository;
    }

    public UserDetails saveUserDetails(UserDetails userDetails) {
        return userDetailsRepository.save(userDetails);
    }

    public Optional<UserDetails> findById(Long id) {
        return userDetailsRepository.findById(id);
    }

    public UserDetails findByUser(User user) {
        return userDetailsRepository.findByUser(user);
    }
}
