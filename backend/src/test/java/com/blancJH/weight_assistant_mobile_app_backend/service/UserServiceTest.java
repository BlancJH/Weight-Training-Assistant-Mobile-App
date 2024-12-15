package com.blancJH.weight_assistant_mobile_app_backend.service;

import com.blancJH.weight_assistant_mobile_app_backend.model.User;
import com.blancJH.weight_assistant_mobile_app_backend.repository.MemoryUserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

class UserServiceTest {

    private UserService userService;

    @BeforeEach
    void setUp() {
        // Initialise UserService with MemoryUserRepository
        userService = new UserService();
    }

    @Test
    void testJoin() {
        // Create a new user
        User user = new User();
        user.setUsername("John Doe");
        user.setEmail("johndoe@example.com");
        user.setPassword("securepassword");

        // Register the user
        long userId = userService.join(user);

        // Verify the user is registered
        Optional<User> registeredUser = userService.findOne(userId);
        Assertions.assertTrue(registeredUser.isPresent());
        Assertions.assertEquals("johndoe@example.com", registeredUser.get().getEmail());
    }

    @Test
    void testDuplicateEmail() {
        // Create and register the first user
        User user1 = new User();
        user1.setUsername("John Doe");
        user1.setEmail("duplicate@example.com");
        user1.setPassword("password1");
        userService.join(user1);

        // Create a second user with the same email
        User user2 = new User();
        user2.setUsername("Jane Doe");
        user2.setEmail("duplicate@example.com");
        user2.setPassword("password2");

        // Verify an exception is thrown when registering a user with a duplicate email
        Assertions.assertThrows(IllegalStateException.class, () -> userService.join(user2));
    }

    @Test
    void testFindUsers() {
        // Register two users
        User user1 = new User();
        user1.setUsername("User1");
        user1.setEmail("user1@example.com");
        user1.setPassword("password1");
        userService.join(user1);

        User user2 = new User();
        user2.setUsername("User2");
        user2.setEmail("user2@example.com");
        user2.setPassword("password2");
        userService.join(user2);

        // Verify all users can be retrieved
        Assertions.assertEquals(2, userService.findUsers().size());
    }

    @Test
    void testFindOne() {
        // Register a user
        User user = new User();
        user.setUsername("Single User");
        user.setEmail("singleuser@example.com");
        user.setPassword("password");
        long userId = userService.join(user);

        // Verify the user can be found by ID
        Optional<User> foundUser = userService.findOne(userId);
        Assertions.assertTrue(foundUser.isPresent());
        Assertions.assertEquals("Single User", foundUser.get().getUsername());
    }
}
