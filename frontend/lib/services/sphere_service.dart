import 'dart:convert';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:http/http.dart' as http;
import 'auth_service.dart';

class SphereService {
  final String _baseUrl = dotenv.env['BACKEND_BASE_URL'] ?? "http://default-url.com";
  final AuthService _authService = AuthService();

  /// Fetch the representator sphere for the authenticated user.
  Future<Map<String, dynamic>> fetchRepresentator() async {
    // Retrieve the JWT token from the AuthService.
    final jwtToken = await _authService.getToken();
    if (jwtToken == null) {
      throw Exception("JWT token not found. User must log in.");
    }

    // Construct the URL for the endpoint.
    final url = Uri.parse('$_baseUrl/user-spheres/representator/get');
    final response = await http.get(
      url,
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer $jwtToken',
      },
    );

    print("JWT Sent: $jwtToken");
    print("Response: ${response.statusCode} - ${response.body}");

    if (response.statusCode == 200) {
      // Decode the JSON response.
      return jsonDecode(response.body) as Map<String, dynamic>;
    } else {
      throw Exception('Failed to fetch representator: ${response.statusCode} ${response.body}');
    }
  }

  /// Update the representator sphere for the authenticated user.
  /// This sends a PUT request to the backend with userId and sphereId as query parameters.
  Future<void> updateRepresentator({
    required int sphereId,
  }) async {
    // Retrieve the JWT token from the AuthService.
    final jwtToken = await _authService.getToken();
    if (jwtToken == null) {
      throw Exception("JWT token not found. User must log in.");
    }

    // Construct the URL with sphereId only.
    final url = Uri.parse('$_baseUrl/user-spheres/representator/set?sphereId=$sphereId');

    final response = await http.put(
      url,
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer $jwtToken',
      },
    );

    print("JWT Sent: $jwtToken");
    print("PUT Response: ${response.statusCode} - ${response.body}");

    if (response.statusCode != 200) {
      throw Exception('Failed to update representator: ${response.statusCode} ${response.body}');
    }
  }

  /// Fetch the list of user-owned spheres for the authenticated user.
  /// The backend returns a JSON array of UserSphere objects.
  /// Each object is transformed into a simple map with keys 'name' and 'level'.
  Future<List<Map<String, dynamic>>> fetchSpheres() async {
    final jwtToken = await _authService.getToken();
    if (jwtToken == null) {
      throw Exception("JWT token not found. User must log in.");
    }

    final url = Uri.parse('$_baseUrl/user-spheres/get-all');
    final response = await http.get(
      url,
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer $jwtToken',
      },
    );

    print("JWT Sent: $jwtToken");
    print("Response: ${response.statusCode} - ${response.body}");

    if (response.statusCode == 200) {
      final decoded = jsonDecode(response.body);
      print("Decoded JSON: $decoded");
      print("Decoded JSON type: ${decoded.runtimeType}");

      if (decoded is List) {
        // Transform each UserSphere object.
        return decoded.map<Map<String, dynamic>>((userSphere) {
          // Check if there's a nested 'sphere' object.
          final nestedSphere = userSphere['sphere'];
          // If nestedSphere is not null, use its 'sphereName'; otherwise, fall back to the top-level 'sphereName'.
          final String sphereName = nestedSphere != null 
              ? nestedSphere['sphereName'] 
              : userSphere['sphereName'];
          return {
            'id': userSphere['id'],
            'name': sphereName,
            'level': userSphere['level'],
            'quantity': userSphere['quantity'],
            'representator': userSphere['representator']
          };
        }).toList();
      } else {
        throw Exception('Expected a List but got: ${decoded.runtimeType}');
      }
    } else {
      throw Exception('Failed to fetch spheres: ${response.statusCode} ${response.body}');
    }
  }

}