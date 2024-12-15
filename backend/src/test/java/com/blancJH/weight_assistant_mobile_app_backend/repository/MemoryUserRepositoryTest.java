package com.blancJH.weight_assistant_mobile_app_backend.repository;

import com.blancJH.weight_assistant_mobile_app_backend.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

class MemoryUserRepositoryTest {
    MemoryUserRepository repository = new MemoryUserRepository();

    @Test
    public void save(){
        User user =new User();
        user.setEmail("abcd@efg.com");

        repository.save(user);

        User result = repository.findById(user.getId()).get();
        Assertions.assertEquals(user, result);
    }
}