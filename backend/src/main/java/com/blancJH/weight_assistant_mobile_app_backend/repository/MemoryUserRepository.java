package com.blancJH.weight_assistant_mobile_app_backend.repository;

import com.blancJH.weight_assistant_mobile_app_backend.repository.UserRepository; 
import com.blancJH.weight_assistant_mobile_app_backend.model.User;

import java.util.Map;
import java.util.Optional;
import java.util.List;
import java.util.HashMap; 
import java.util.ArrayList;

public class MemoryUserRepository implements UserRepository {

    private static Map<Long, User> store = new HashMap<>();
    private static long sequence = 0L;

    @Override
    public User save(User user) {
        user.setId(++sequence);
        store.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return store.values().stream()
                .filter(user -> user.getEmail().equals(email))
                .findAny();
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return store.values().stream()
                .filter(user -> user.getUsername().equals(username))
                .findAny();
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(store.values());
    }
}   