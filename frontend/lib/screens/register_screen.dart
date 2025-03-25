// ignore_for_file: prefer_const_constructors

// Flutter material package provides UI components and theming
import 'package:flutter/material.dart';
import 'package:flutter/gestures.dart'; // Needed for TapGestureRecognizer
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
  // Consent flags
  bool _consentUsage = false;
  bool _consentAnalysis = false;

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
          autovalidateMode: AutovalidateMode.onUserInteraction, // Live validation enabled
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              // User Name Field
              CustomTextField(
                labelText: 'User Name',
                controller: _nameController,
                validator: (value) {
                  // Show "required" error only after submission.
                  if (value == null || value.trim().isEmpty) {
                    return _submitted ? 'Name is required' : null;
                  }
                  return null;
                },
              ),
              SizedBox(height: 16.0),

              // Email Field
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

              // Password Field
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

              // Repeat Password Field
              CustomTextField(
                labelText: 'Repeat Password',
                controller: _repeatPasswordController,
                obscureText: true,
                validator: (value) {
                  if (value == null || value.trim().isEmpty) {
                    return _submitted ? 'Repeat Password is required' : null;
                  }
                  return Validators.validateMatch(
                      value, _passwordController.text, 'Repeat Password');
                },
              ),
              SizedBox(height: 16.0),

              // Consent for User Registration and Data Usage as a FormField
              FormField<bool>(
                initialValue: _consentUsage,
                validator: (value) {
                  if (value != true) {
                    return 'You must agree to the User Registration and Data Usage Consent';
                  }
                  return null;
                },
                builder: (FormFieldState<bool> state) {
                  return Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Row(
                        children: [
                          Checkbox(
                            value: _consentUsage,
                            onChanged: (bool? newValue) {
                              setState(() {
                                _consentUsage = newValue ?? false;
                                state.didChange(newValue);
                              });
                            },
                          ),
                          Expanded(
                            child: RichText(
                              text: TextSpan(
                                text: 'I agree to the ',
                                style: TextStyle(color: Colors.black),
                                children: [
                                  TextSpan(
                                    text: 'User Registration and Data Usage Consent',
                                    style: TextStyle(
                                      color: Colors.blue,
                                      decoration: TextDecoration.underline,
                                    ),
                                    recognizer: TapGestureRecognizer()
                                      ..onTap = () {
                                        // TODO: Navigate to or open the User Registration and Data Usage Consent document.
                                      },
                                  ),
                                ],
                              ),
                            ),
                          ),
                        ],
                      ),
                      if (state.hasError)
                        Padding(
                          padding: EdgeInsets.only(left: 16.0),
                          child: Text(
                            state.errorText!,
                            style: TextStyle(color: Colors.red, fontSize: 12),
                          ),
                        ),
                    ],
                  );
                },
              ),
              SizedBox(height: 8.0),

              // Consent for Data Analysis and Model Training as a FormField
              FormField<bool>(
                initialValue: _consentAnalysis,
                validator: (value) {
                  if (value != true) {
                    return 'You must agree to the Data Analysis and Model Training Consent';
                  }
                  return null;
                },
                builder: (FormFieldState<bool> state) {
                  return Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Row(
                        children: [
                          Checkbox(
                            value: _consentAnalysis,
                            onChanged: (bool? newValue) {
                              setState(() {
                                _consentAnalysis = newValue ?? false;
                                state.didChange(newValue);
                              });
                            },
                          ),
                          Expanded(
                            child: RichText(
                              text: TextSpan(
                                text: 'I agree to the ',
                                style: TextStyle(color: Colors.black),
                                children: [
                                  TextSpan(
                                    text: 'Data Analysis and Model Training Consent',
                                    style: TextStyle(
                                      color: Colors.blue,
                                      decoration: TextDecoration.underline,
                                    ),
                                    recognizer: TapGestureRecognizer()
                                      ..onTap = () {
                                        // TODO: Navigate to or open the Data Analysis and Model Training Consent document.
                                      },
                                  ),
                                ],
                              ),
                            ),
                          ),
                        ],
                      ),
                      if (state.hasError)
                        Padding(
                          padding: EdgeInsets.only(left: 16.0),
                          child: Text(
                            state.errorText!,
                            style: TextStyle(color: Colors.red, fontSize: 12),
                          ),
                        ),
                    ],
                  );
                },
              ),
              SizedBox(height: 24.0),

              // Register Button
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
