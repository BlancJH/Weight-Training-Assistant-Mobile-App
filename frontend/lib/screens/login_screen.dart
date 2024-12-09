// Import the module which contains pre-designed UI components
import 'package:flutter/material.dart';

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
          ],
        ),
      ),
    );
  }
}
