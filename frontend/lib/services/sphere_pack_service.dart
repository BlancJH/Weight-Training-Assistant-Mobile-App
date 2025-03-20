import 'dart:convert';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:http/http.dart' as http;
import 'auth_service.dart';

class SpherePackService {
  final String _baseUrl =
      dotenv.env['BACKEND_BASE_URL'] ?? "http://default-url.com";
  final AuthService _authService = AuthService();

  /// Generates a sphere pack and saves it to the user's collection.
  ///
  /// [packType] should correspond to a valid pack type (e.g., "COMMON", "RARE").
  /// [packSize] is the number of spheres to generate (defaults to 3 if not specified).
  Future<String> generateAndSaveSpherePack({
    required String packType,
    int packSize = 3,
  }) async {
    // Retrieve JWT token from the AuthService.
    final jwtToken = await _authService.getToken();
    if (jwtToken == null) {
      throw Exception("JWT token not found. User must log in.");
    }

    // Construct the URL with query parameters for packType and packSize.
    final url = Uri.parse(
        '$_baseUrl/sphere-packs/generate-and-save?packType=${packType.toUpperCase()}&packSize=$packSize');

    // Make the POST request to the backend.
    final response = await http.post(
      url,
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer $jwtToken',
      },
    );

    print("JWT Sent: $jwtToken");
    print("Response: ${response.statusCode} - ${response.body}");

    if (response.statusCode == 200) {
      // Return the success message from the backend.
      return response.body;
    } else {
      throw Exception(
          'Failed to generate and save sphere pack: ${response.statusCode} ${response.body}');
    }
  }
}
