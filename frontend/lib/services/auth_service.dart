import 'dart:convert'; // For JSON encoding/decoding
import 'package:http/http.dart' as http;

class AuthService {
  // Base URL for your backend
  final String _baseUrl = 'http://localhost:8080/api/auth';

  Future<http.Response> registerUser({
    required String username,
    required String email,
    required String password,
  }) async {
    final url = '$_baseUrl/register';

    try {
      final body = json.encode({
        'username': username,
        'email': email,
        'password': password,
      });

      final response = await http.post(
        Uri.parse(url),
        headers: {'Content-Type': 'application/json'},
        body: body,
      );

      // Check for success
      if (response.statusCode == 200) {
        return response;
      } else {
        throw Exception('Failed to register user: ${response.body}');
      }
    } catch (e) {
      // Log the error and rethrow
      print('Error during registration: $e');
      throw Exception('Failed to register user: $e');
    }
  }

  Future<http.Response> loginUser({
    required String email,
    required String password,
  }) async {
    final url = '$_baseUrl/login';

    try {
      final body = json.encode({
        'email': email,
        'password': password,
      });

      final response = await http.post(
        Uri.parse(url),
        headers: {'Content-Type': 'application/json'},
        body: body,
      );

      return response; // Return the response to the caller
    } catch (e) {
      throw Exception('Failed to login: $e');
    }
  }
}
