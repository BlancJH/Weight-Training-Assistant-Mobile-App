// ignore_for_file: prefer_const_constructors

// Flutter material package provides UI components and theming
import 'package:flutter/material.dart';
import '../widgets/submit_button.dart';
import '../utils/http_requester.dart';
import '../utils/validator.dart';
import '../widgets/custom_text_field.dart';
import '../services/auth_service.dart';
import 'login_screen.dart';

// Define a stateful widget for the Register screen
class RegisterScreen extends StatefulWidget {
  @override
  _RegisterScreenState createState() => _RegisterScreenState();
}

// State class to manage form fields, validation, and user interactions
class _RegisterScreenState extends State<RegisterScreen> {
  // Key to uniquely identify the form and validate it
  final _formKey = GlobalKey<FormState>();
  final _nameController = TextEditingController();
  final _emailController = TextEditingController();
  final _passwordController = TextEditingController();
  final _repeatPasswordController = TextEditingController();
  final AuthService _authService = AuthService();

  // Flag to control when required field errors are shown
  bool _submitted = false;

  Future<void> _registerUser() async {
    setState(() {
      _submitted = true; // User pressed submit, show required errors now.
    });
    
    if (!_formKey.currentState!.validate()) return;

    await submitRequest(
      context: context,
      request: () => _authService.registerUser(
        username: _nameController.text.trim(),
        email: _emailController.text.trim(),
        password: _passwordController.text.trim(),
      ),
      successMessage: 'Registration successful!',
      onSuccess: () {
        Navigator.pushReplacement(
          context,
          MaterialPageRoute(builder: (context) => LoginScreen()),
        );
      },
    );
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    // Scaffold provides the basic visual structure for the screen
    return Scaffold(
      appBar: AppBar(
        // App bar at the top with the screen title
        backgroundColor: theme.colorScheme.background,
        title: Text('Register'),
      ),
      // Padding adds space around the form
      body: Padding(
        padding: EdgeInsets.all(16.0),
        child: Form(
          key: _formKey,
          // Live validation for non-required validations remains enabled.
          autovalidateMode: AutovalidateMode.onUserInteraction,
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              // Name input field
              CustomTextField(
                labelText: 'User Name',
                controller: _nameController,
                validator: (value) {
                  // Show "required" error only after submission.
                  if (value == null || value.trim().isEmpty) {
                    return _submitted ? 'Name is required' : null;
                  }
                  // You can add additional validations here if needed.
                  return null;
                },
              ),
              SizedBox(height: 16.0),

              // Email input field
              CustomTextField(
                labelText: 'Email',
                controller: _emailController,
                validator: (value) {
                  // First, check for required condition.
                  if (value == null || value.trim().isEmpty) {
                    return _submitted ? 'Email is required' : null;
                  }
                  // Then, perform live email format validation.
                  return Validators.validateEmail(value);
                },
              ),
              SizedBox(height: 16.0),

              // Password input field
              CustomTextField(
                labelText: 'Password',
                controller: _passwordController,
                obscureText: true,
                validator: (value) {
                  if (value == null || value.trim().isEmpty) {
                    return _submitted ? 'Password is required' : null;
                  }
                  return Validators.validatePassword(value);
                },
              ),
              SizedBox(height: 16.0),

              // Repeat password input field
              CustomTextField(
                labelText: 'Repeat Password',
                controller: _repeatPasswordController,
                obscureText: true,
                validator: (value) {
                  if (value == null || value.trim().isEmpty) {
                    return _submitted ? 'Repeat Password is required' : null;
                  }
                  return Validators.validateMatch(value, _passwordController.text, 'Repeat Password');
                },
              ),
              SizedBox(height: 24.0),

              // Register button
              SubmitButton(
                text: 'Register',
                onPressed: _registerUser,
              ),
            ],
          ),
        ),
      ),
    );
  }
}
