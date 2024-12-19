import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;

Future<void> submitRequest({
  required BuildContext context,
  required Future<http.Response> Function() request,
  required String successMessage,
  required VoidCallback onSuccess,
}) async {
  // Show a loading indicator
  ScaffoldMessenger.of(context).showSnackBar(
    SnackBar(content: Text('Processing...')),
  );

  try {
    // Execute the HTTP request
    final response = await request();

    // Dismiss the loading indicator
    ScaffoldMessenger.of(context).hideCurrentSnackBar();

    if (response.statusCode == 200) {
      // Show success message
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text(successMessage)),
      );
      // Perform success callback
      onSuccess();
    } else {
      // Handle failure
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('Failed: ${response.body}')),
      );
    }
  } catch (e) {
    // Handle errors
    ScaffoldMessenger.of(context).hideCurrentSnackBar();
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(content: Text('Error: $e')),
    );
  }
}

}