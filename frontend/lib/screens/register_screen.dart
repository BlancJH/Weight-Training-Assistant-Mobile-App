// ignore_for_file: prefer_const_constructors

// Flutter material package provides UI components and theming
import 'package:flutter/material.dart';

// Define a stateful widget for the Register screen
class RegisterScreen extends StatefulWidget {
  @override
  _RegisterScreenState createState() => _RegisterScreenState(); // Create the state
}

// State class to manage form fields, validation, and user interactions
class _RegisterScreenState extends State<RegisterScreen> {
  // Key to uniquely identify the form and validate it
  final _formKey = GlobalKey<FormState>();

  // Controller to capture and manage the input for the password field
  final _passwordController = TextEditingController();

  @override
  Widget build(BuildContext context) {
    // Scaffold provides the basic visual structure for the screen
    return Scaffold(
      appBar: AppBar(
        // App bar at the top with the screen title
        title: Text('Register'),
      ),
      // Padding adds space around the form
      body: Padding(
        padding: EdgeInsets.all(16.0), // 16 pixels padding on all sides
        child: Form(
          key: _formKey, // Attach the form key
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.stretch, // Stretch child widgets to full width
            children: [
              // Name input field
              TextFormField(
                decoration: InputDecoration(
                  labelText: 'Name', // Label text for the field
                  border: OutlineInputBorder(), // Adds a border around the input
                ),
                validator: (value) {
                  // Validation logic to check if the name is empty
                  if (value == null || value.isEmpty) {
                    return 'Name is required'; // Error message if validation fails
                  }
                  return null; // Validation passed
                },
              ),
              SizedBox(height: 16.0), // Add vertical space between fields

              // Email input field
              TextFormField(
                decoration: InputDecoration(
                  labelText: 'Email', // Label text for the field
                  border: OutlineInputBorder(), // Adds a border around the input
                ),
                validator: (value) {
                  // Validation logic to check if the email is empty
                  if (value == null || value.isEmpty) {
                    return 'Email is required'; // Error message if validation fails
                  }
                  return null; // Validation passed
                },
              ),
              SizedBox(height: 16.0), // Add vertical space between fields

              // Password input field
              TextFormField(
                controller: _passwordController, // Connects the field to the controller
                decoration: InputDecoration(
                  labelText: 'Password', // Label text for the field
                  border: OutlineInputBorder(), // Adds a border around the input
                ),
                obscureText: true, // Masks the input for privacy (e.g., "••••")
                validator: (value) {
                  // Validation logic to check if the password is empty
                  if (value == null || value.isEmpty) {
                    return 'Password is required'; // Error message if validation fails
                  }
                  return null; // Validation passed
                },
              ),
              SizedBox(height: 16.0), // Add vertical space between fields

              // Repeat password input field
              TextFormField(
                decoration: InputDecoration(
                  labelText: 'Repeat Password', // Label text for the field
                  border: OutlineInputBorder(), // Adds a border around the input
                ),
                obscureText: true, // Masks the input for privacy (e.g., "••••")
                validator: (value) {
                  // Validation logic for the repeat password field
                  if (value == null || value.isEmpty) {
                    return 'Please repeat your password'; // Error message if field is empty
                  } else if (value != _passwordController.text) {
                    return 'Passwords do not match'; // Error message if passwords mismatch
                  }
                  return null; // Validation passed
                },
              ),
              SizedBox(height: 24.0), // Add more space before the button

              // Register button
              ElevatedButton(
                onPressed: () {
                  // Check if the form is valid
                  if (_formKey.currentState!.validate()) {
                    // All fields passed validation
                    print('Form is valid'); // Debug output
                  } else {
                    // Validation failed
                    print('Form is invalid'); // Debug output
                  }
                },
                child: Text('Register'), // Button text
              ),
            ],
          ),
        ),
      ),
    );
  }
}
