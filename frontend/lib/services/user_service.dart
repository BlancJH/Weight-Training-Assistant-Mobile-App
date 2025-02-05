import 'dart:convert';
import 'package:http/http.dart' as http;
import 'package:jwt_decoder/jwt_decoder.dart';
import 'auth_service.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';

class UserService {
  // Base URL for your backend (not the auth portion, but the user portion)
  final String? _baseUrl = dotenv.env['BACKEND_BASE_URL']?? "http://default-url.com";

  // Create an instance of AuthService to get the token
  final AuthService _authService = AuthService();

  /// Save or update user details using the backend
  Future<void> saveOrUpdateUserDetails(Map<String, dynamic> userDetails) async {
    final token = await _authService.getToken();

    if (token == null) {
      throw Exception('No JWT token found. User might not be logged in.');
    }

    final url = '$_baseUrl/v1/userDetails';

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

  /// Fetch user details from the backend
  Future<Map<String, dynamic>> fetchUserDetails() async {
    final token = await _authService.getToken();

    if (token == null) {
      throw Exception('No JWT token found. User might not be logged in.');
    }

    final url = '$_baseUrl/userDetails';

    try {
      final response = await http.get(
        Uri.parse(url),
        headers: {
          'Authorization': 'Bearer $token',
        },
      );

      if (response.statusCode == 200) {
        final data = json.decode(response.body);
        print('Fetched user details successfully: $data');
        return data;
      } else {
        print('Failed to fetch user details: ${response.body}');
        throw Exception('Failed to fetch user details');
      }
    } catch (e) {
      print('Error fetching user details: $e');
      throw Exception('Error fetching user details: $e');
    }
  }

}
