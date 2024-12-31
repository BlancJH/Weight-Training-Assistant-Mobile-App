package com.blancJH.weight_assistant_mobile_app_backend.repository;

import com.blancJH.weight_assistant_mobile_app_backend.model.User;
import com.blancJH.weight_assistant_mobile_app_backend.model.UserDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDetailsRepository extends JpaRepository<UserDetails, Long> {
    UserDetails findByUser(User user);
}
