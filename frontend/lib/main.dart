import 'package:flutter/material.dart';
import 'screens/login_screen.dart'; // Import the login screen
import 'screens/home_screen.dart'; // Import the home screen

void main() {
  runApp(MyApp());
}

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'My App',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      initialRoute: '/',
      routes: {
        '/': (context) => LoginScreen(),
        '/login': (context) => LoginScreen(), // Add this route
        '/home': (context) => HomeScreen(),
      },
    );
  }
}