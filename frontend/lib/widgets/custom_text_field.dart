import 'package:flutter/material.dart';

class CustomTextField extends StatelessWidget {
  final String labelText;
  final TextEditingController controller;
  final String? Function(String?)? validator;
  final bool obscureText;
  final void Function(String)? onChanged;
  final int? maxLength; // Optional max length for character count

  const CustomTextField({
    required this.labelText,
    required this.controller,
    this.validator,
    this.obscureText = false,
    this.onChanged,
    this.maxLength, // Optional parameter for max length
  });

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        TextFormField(
          controller: controller,
          decoration: InputDecoration(
            labelText: labelText,
            border: const OutlineInputBorder(),
            counterText: '', // Hide default counter
          ),
          obscureText: obscureText,
          validator: validator,
          onChanged: onChanged,
        ),
        if (maxLength != null)
          Align(
            alignment: Alignment.centerRight,
            child: Text(
              '${controller.text.length} / $maxLength',
              style: const TextStyle(
                fontSize: 12.0,
                color: Colors.grey,
              ),
            ),
          ),
      ],
    );
  }
}
