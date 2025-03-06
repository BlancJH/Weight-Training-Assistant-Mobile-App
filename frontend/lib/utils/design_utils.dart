import 'package:flutter/material.dart';

// Primary colors
const Color primaryColor = Color(0xFF0B1A30);  // Deep Space Navy
const Color secondaryColor = Color(0xFFA239CA); // Electric Purple

// Background and Surface
const Color backgroundColor = Color(0xFF050A30); // Space Black
const Color surfaceColor = Color(0xFF050A30); // Asteroid Grey

// Highlights
const Color highlightColor = Color(0xFFFF3860); // Neon Blue
const Color buttonColor = Color(0xFFFF3860); 

// Text colors
const Color primaryTextColor = Color(0xFFEAEAEA); // Light Grey
const Color secondaryTextColor = Color(0xFFB4B4B4); // Purple Grey

// ThemeData
final ThemeData spaceTheme = ThemeData(
  brightness: Brightness.dark,
  primaryColor: primaryColor,
  scaffoldBackgroundColor: backgroundColor,
  colorScheme: ColorScheme.dark(
    primary: primaryColor,
    secondary: secondaryColor,
    background: backgroundColor,
    surface: surfaceColor,
    onPrimary: primaryTextColor,
    onSecondary: primaryTextColor,
    onSurface: primaryTextColor,
    onBackground: primaryTextColor,
  ),
  textTheme: const TextTheme(
    bodyLarge: TextStyle(color: primaryTextColor),
    bodyMedium: TextStyle(color: secondaryTextColor),
  ),
  elevatedButtonTheme: ElevatedButtonThemeData(
    style: ElevatedButton.styleFrom(
      backgroundColor: buttonColor,
      foregroundColor: Colors.white,
    ),
  ),
);
