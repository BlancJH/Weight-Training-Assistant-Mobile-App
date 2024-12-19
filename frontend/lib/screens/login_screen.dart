// Import the module which contains pre-designed UI components
import 'package:flutter/material.dart';
import 'register_screen.dart'; // Import register screen
import '../services/auth_service.dart'; // Import AuthService
import '../utils/validator.dart'; // Import Validator
import '../widgets/custom_text_field.dart'; // Import custom text field
import '../widgets/submit_button.dart'; // Import submit button
import '../utils/http_requester.dart'; // Import http requester
import 'home_screen.dart'; // Import home screen

// Create LoginScreen class // Modify to stateful widget
class LoginScreen extends StatefulWidget {
  @override
  _LoginScreenState createState() => _LoginScreenState();
}

class _LoginScreenState extends State<LoginScreen> {
  final TextEditingController _emailController = TextEditingController();
  final TextEditingController _passwordController = TextEditingController();
  final _formKey = GlobalKey<FormState>(); // Form key for validation
  final AuthService _authService = AuthService(); // Instance of AuthService

  Future<void> _loginUser() async {
    if (!_formKey.currentState!.validate()) return;

    await submitRequest(
      context: context,
      request: () => _authService.loginUser(
        email: _emailController.text.trim(),
        password: _passwordController.text.trim(),
      ),
      successMessage: 'Login successful!',
      onSuccess: () {
        _emailController.clear();
        _passwordController.clear();
        Navigator.pushReplacement(
          context,
          MaterialPageRoute(builder: (context) => HomeScreen()),
        );
      },
    );
  }

  @override // The method is overriding a parent class method
  Widget build(BuildContext context) {
    return Scaffold( // Scaffold: Basic structure fo UI
      appBar: AppBar( // AppBar: Bar at the top of the screen
        title: Text('Login'),
      ),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child:Form(
          key: _formKey, // Attach the form key
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.stretch, // Make buttons full-width
            children: [
              // Email input field
              CustomTextField(
                labelText: 'Email',
                controller: _emailController,
                validator: (value) => Validators.validateEmail(value)
              ),
              SizedBox(height: 16.0),

              // Password input field
              CustomTextField(
                labelText: 'Password',
                controller: _passwordController,
                validator: Validators.validatePassword,
                obscureText: true,
              ),
              SizedBox(height: 16.0), // Add vertical space between fields

              // Login button
              SubmitButton(
                text: 'Login',
                onPressed: _loginUser,
              ),
              SizedBox(height: 10), // Add spacing between buttons

              SubmitButton(
                text: 'Register', // Button label
                onPressed: () {
                  Navigator.push(
                    context,
                    MaterialPageRoute(builder: (context) => RegisterScreen()), // Navigate to the Register screen
                  );
                },
              ),
            ],
          ),
        ),
      ),
    );
  }
}
