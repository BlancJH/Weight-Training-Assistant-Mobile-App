// Import the module which contains pre-designed UI components
import 'package:flutter/material.dart';
import 'register_screen.dart'; // Import register screen
import '../services/auth_service.dart'; // Import AuthService
import '../utils/validator.dart'; // Import Validator
import '../widgets/custom_text_field.dart'; // Import custom text field
import '../widgets/submit_button.dart'; // Import submit button
import '../utils/http_requester.dart'; // Import http requester
import 'home_screen.dart'; // Import home screen
import 'package:jwt_decoder/jwt_decoder.dart'; // Import JWT decoder
import '../utils/design_utils.dart';

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
  bool _isLoading = false; // Loading status

  void _clearControllers() {
    _emailController.clear();
    _passwordController.clear();
  }

Future<void> _loginUser() async {
  if (_formKey.currentState?.validate() ?? false) {
    setState(() {
      _isLoading = true;
    });
    try {
      final token = await _authService.loginUser(
        email: _emailController.text.trim(),
        password: _passwordController.text.trim(),
      );

      if (token != null) {
        // Decode the JWT payload
        Map<String, dynamic> decodedToken = JwtDecoder.decode(token);

        print('Decoded Token: $decodedToken'); // Debug: Log the payload

        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Login successful!')),
        );

        Navigator.pushReplacement(
          context,
          MaterialPageRoute(builder: (context) => HomeScreen()),
        );
      }
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('Login failed: $e')),
      );
    } finally {
      setState(() {
        _isLoading = false;
      });
    }
  }
}


  @override // The method is overriding a parent class method
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    return Scaffold( // Scaffold: Basic structure fo UI
      appBar: AppBar( // AppBar: Bar at the top of the screen
        backgroundColor: theme.colorScheme.surface,
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
                text: 'Register',
                onPressed: () {
                  Navigator.push(
                    context,
                    MaterialPageRoute(
                      builder: (context) => RegisterScreen(),
                    ),
                  ).then((_) => _clearControllers());
                },
              ),
            ],
          ),
        ),
      ),
    );
  }
}
