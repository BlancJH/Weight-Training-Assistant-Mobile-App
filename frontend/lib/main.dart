import 'package:flutter/material.dart';
import 'screens/login_screen.dart'; // Import the login screen

void main() {
  runApp(MyApp());
}

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false, // Hides the debug banner
      home: LoginScreen(),              // Sets LoginScreen as the starting page
    );
  }
}
