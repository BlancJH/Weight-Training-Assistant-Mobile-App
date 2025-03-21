import 'package:flutter/material.dart';
import '../utils/design_utils.dart';

class SubmitButton extends StatelessWidget {
  final String text;
  final VoidCallback? onPressed;
  final Color backgroundColor;
  final Color textColor;
  final double borderRadius;
  final double? width;

  const SubmitButton({
    required this.text,
    required this.onPressed,
    this.backgroundColor = buttonColor, // default active color
    this.textColor = primaryTextColor,
    this.borderRadius = 8.0,
    this.width, 
    Key? key,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    // Use a different background color and text color when disabled
    final Color effectiveBackgroundColor = onPressed == null ? Colors.grey : backgroundColor;
    final Color effectiveTextColor = onPressed == null ? Colors.white70 : textColor;

    return SizedBox(
      width: width ?? double.infinity,
      child: ElevatedButton(
        onPressed: onPressed, // if null, button is disabled
        style: ElevatedButton.styleFrom(
          backgroundColor: effectiveBackgroundColor,
          foregroundColor: effectiveTextColor,
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(borderRadius),
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
