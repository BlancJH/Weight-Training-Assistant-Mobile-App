import 'package:flutter/material.dart';

class CustomTextField extends StatelessWidget {
  final String labelText;
  final TextEditingController controller;
  final String? Function(String?)? validator;
  final bool obscureText;
  final void Function(String)? onChanged; // Added onChanged callback

  const CustomTextField({
    required this.labelText,
    required this.controller,
    this.validator,
    this.obscureText = false,
    this.onChanged, // Optional parameter for onChanged
  });

  @override
  Widget build(BuildContext context) {
    return TextFormField(
      controller: controller,
      decoration: InputDecoration(
        labelText: labelText,
        border: OutlineInputBorder(),
      ),
      obscureText: obscureText,
      validator: validator,
      autocorrect: false,
      enableSuggestions: false,
      onChanged: onChanged, // Trigger callback when text changes
    );
  }
}
