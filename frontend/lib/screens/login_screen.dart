// Import the module which contains pre-designed UI components
import 'package:flutter/material.dart';
import 'register_screen.dart'; // Import register screen

// Create LoginScreen class // StatelessWidget: immutable, not change state( Look into it later )
class LoginScreen extends StatelessWidget {
  // Controllers for managing input
  final TextEditingController emailController = TextEditingController();
  final TextEditingController passwordController = TextEditingController();

  @override // The method is overriding a parent class method
  Widget build(BuildContext context) {
    return Scaffold( // Scaffold: Basic structure fo UI
      appBar: AppBar( // AppBar: Bar at the top of the screen
        title: Text('Login'),
      ),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            TextField( //TextField : Text bar for user input
              controller: emailController, // Link to emailController
              decoration: InputDecoration(labelText: 'Email'), // Add label
            ),
            TextField(
              controller: passwordController,
              decoration: InputDecoration(labelText: 'Password'),
              obscureText: true,
            ),
            SizedBox(height: 20),
            ElevatedButton(
              onPressed: () {
                // Call login logic
              },
              child: Text('Login'),
            ),
            SizedBox(height: 10), // Add spacing between buttons
            ElevatedButton(
              onPressed: () {
                // Navigate to the Register screen
                Navigator.push(
                  context,
                  MaterialPageRoute(builder: (context) => RegisterScreen()),
                );
              },
              style: ElevatedButton.styleFrom(
                backgroundColor: Colors.grey, // Optional: Customize button color
              ),
              child: Text('Register'),
            ),
          ],
        ),
      ),
    );
  }
}
