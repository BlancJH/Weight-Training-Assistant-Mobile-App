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
  _RegisterScreenState createState() => _RegisterScreenState(); // Create the state
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

  Future<void> _registerUser() async {
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
        padding: EdgeInsets.all(16.0), // 16 pixels padding on all sides
        child: Form(
          key: _formKey, // Attach the form key
          autovalidateMode: AutovalidateMode.onUserInteraction, // Enable live validation
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.stretch, // Stretch child widgets to full width
            children: [

              // Name input field
              CustomTextField(
                labelText: 'User Name', // Label text for the field
                controller: _nameController,
                validator: (value) => Validators.validateRequired(value, 'Name'),
              ),
              SizedBox(height: 16.0), // Add vertical space between fields

              // Email input field
              CustomTextField(
                labelText: 'Email',
                controller: _emailController,
                validator: (value) => Validators.validateEmail(value)
              ),
              SizedBox(height: 16.0), // Add vertical space between fields

              // Password input field
              CustomTextField(
                labelText: 'Password',
                controller: _passwordController,
                validator: Validators.validatePassword,
                obscureText: true,
              ),
              SizedBox(height: 16.0), // Add vertical space between fields

              // Repeat password input field
              CustomTextField(
                labelText: 'Repeat Password',
                controller: _repeatPasswordController,
                validator: (value) => Validators.validateMatch(value, _passwordController.text, 'Repeat Password'),
                obscureText: true,
              ),
              SizedBox(height: 24.0), // Add more space before the button

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
