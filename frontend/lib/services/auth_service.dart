import 'dart:convert'; // For JSON encoding/decoding
import 'package:http/http.dart' as http;
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:jwt_decoder/jwt_decoder.dart';

class AuthService {
  // Base URL for your backend
  final String _baseUrl = 'http://localhost:8080/api/auth';
  final FlutterSecureStorage _storage = FlutterSecureStorage();

  // Register User
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

      return response;
    } catch (e) {
      print('Error during registration: $e');
      throw Exception('Failed to register user: $e');
    }
  }

  // Login User
  Future<String> loginUser({
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

      if (response.statusCode == 200) {
        final data = json.decode(response.body);
        final token = data['token'];

        // Save token securely
        await _storage.write(key: 'jwt', value: token);

        return token; // Return the token
      } else {
        final error = json.decode(response.body);
        throw Exception('Login failed: ${error['message'] ?? response.body}');
      }
    } catch (e) {
      print('Error during login: $e');
      throw Exception('Failed to login: $e');
    }
  }

  // Retrieve Token
  Future<String?> getToken() async {
    return await _storage.read(key: 'jwt'); // Get the token from secure storage
  }

  // Logout User
  Future<void> logoutUser() async {
    await _storage.delete(key: 'jwt'); // Delete the token from secure storage
  }

  /// Decode JWT and extract user data
  Future<Map<String, dynamic>?> decodeToken() async {
    final token = await getToken();
    if (token != null) {
      return JwtDecoder.decode(token); // Decodes the JWT
    }
    return null; // Return null if no token exists
  }

}
