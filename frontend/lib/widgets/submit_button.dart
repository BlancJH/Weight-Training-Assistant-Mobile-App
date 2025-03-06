import 'package:flutter/material.dart';
import '../utils/design_utils.dart';

class SubmitButton extends StatelessWidget {
  final String text;
  final VoidCallback onPressed;
  final Color backgroundColor;
  final Color textColor;
  final double borderRadius;
  final double? width;

  const SubmitButton({
    required this.text, // The button's label text
    required this.onPressed, // Callback for button press
    this.backgroundColor = buttonColor, // Default button color
    this.textColor = primaryTextColor, // Default text color
    this.borderRadius = 8.0, // Default rounded corners
    this.width, // Optional custom width
    Key? key,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return SizedBox(
    width: width ?? double.infinity,
    child: ElevatedButton(
      onPressed: onPressed, // Call the provided callback
      style: ElevatedButton.styleFrom(
        backgroundColor: backgroundColor, // Button background color
        foregroundColor: textColor, // Text color
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(borderRadius), // Rounded corners
        ),
        padding: EdgeInsets.symmetric(vertical: 12.0, horizontal: 20.0),
      ),
      child: Text(
        text,
        style: TextStyle(fontSize: 16.0),
      ),
    ),
    );
  }
}
