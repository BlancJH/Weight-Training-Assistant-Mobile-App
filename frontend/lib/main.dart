import 'package:flutter/material.dart';
import 'screens/login_screen.dart'; // Import the login screen
import 'screens/home_screen.dart'; // Import the home screen
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'dart:io';
import 'utils/design_utils.dart';

Future<void> main() async {
  WidgetsFlutterBinding.ensureInitialized();

  print("Current Directory: ${Directory.current.path}"); // Debugging

  try {
    await dotenv.load(fileName: ".env");
    print("Loaded .env successfully");
  } catch (e) {
    print("Failed to load .env: $e");
  }

  runApp(MyApp());
}

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'My App',
      theme: spaceTheme,
      initialRoute: '/',
      routes: {
        '/': (context) => LoginScreen(),
        '/login': (context) => LoginScreen(), // Add this route
        '/home': (context) => HomeScreen(),
      },
    );
  }
}