import 'dart:convert';
import 'package:http/http.dart' as http;
import 'package:jwt_decoder/jwt_decoder.dart';
import 'auth_service.dart';

class UserService {
  // Base URL for your backend (not the auth portion, but the user portion)
  final String _baseUrl = 'http://localhost:8080/api';

  // Create an instance of AuthService to get the token
  final AuthService _authService = AuthService();

  /// Update user profile
  Future<void> updateUserProfileBackend(String userId, Map<String, dynamic> profileData) async {
    final token = await _authService.getToken();
    if (token == null) {
      print('No JWT found. User might not be logged in.');
      throw Exception('Authentication token not found');
    }

    // Construct the endpoint URL: e.g. PUT /api/users/{userId}
    final url = '$_baseUrl/users/$userId';

    try {
      final response = await http.put(
        Uri.parse(url),
        headers: <String, String>{
          'Content-Type': 'application/json',
          'Authorization': 'Bearer $token', // Include JWT in the Auth header
        },
        body: json.encode(profileData),
      );

      if (response.statusCode == 200) {
        print('Backend: User profile updated successfully!');
        print('Server response: ${response.body}');
      } else {
        print('Failed to update user profile on backend: ${response.statusCode}');
        print('Response body: ${response.body}');
      }
    } catch (e) {
      print('Error updating user profile on backend: $e');
      throw Exception('Failed to update user profile on backend: $e');
    }
  }

  /// Update user profile by automatically extracting userId from the JWT
  Future<void> updateUserProfileUsingToken(Map<String, dynamic> profileData) async {
    final token = await _authService.getToken();
    if (token == null) {
      throw Exception('No JWT found. User might not be logged in.');
    }

    // Decode the JWT
    final decodedToken = JwtDecoder.decode(token);

    // Adjust this based on how your JWT is structured.
    final userId = decodedToken['userId'] as String?;
    if (userId == null) {
      throw Exception('userId not found in token');
    }

    // Now send the profile data to your backend
    await updateUserProfileBackend(userId, profileData);
  }
}
