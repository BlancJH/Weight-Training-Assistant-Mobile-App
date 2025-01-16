import 'dart:convert';
import 'package:http/http.dart' as http;
import 'package:jwt_decoder/jwt_decoder.dart';
import 'auth_service.dart';

class UserService {
  // Base URL for your backend (not the auth portion, but the user portion)
  final String _baseUrl = 'http://localhost:8080/api/v1';

  // Create an instance of AuthService to get the token
  final AuthService _authService = AuthService();

  /// Save or update user details using the backend
  Future<void> saveOrUpdateUserDetails(Map<String, dynamic> userDetails) async {
    final token = await _authService.getToken();

    if (token == null) {
      throw Exception('No JWT token found. User might not be logged in.');
    }

    final url = '$_baseUrl/userDetails';

    try {
      final response = await http.post(
        Uri.parse(url),
        headers: {
          'Content-Type': 'application/json',
          'Authorization': 'Bearer $token',
        },
        body: json.encode(userDetails),
      );

      if (response.statusCode != 200) {
        print('Failed to save user details: ${response.body}');
        throw Exception('Failed to save user details');
      }

      print('User details updated successfully!');
    } catch (e) {
      print('Error saving user details: $e');
      throw Exception('Error saving user details: $e');
    }
  }
  
}
