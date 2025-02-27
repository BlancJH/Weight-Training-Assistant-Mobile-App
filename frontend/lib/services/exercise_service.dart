import 'dart:convert';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:http/http.dart' as http;
import '../models/exercise.dart';
import '../services/auth_service.dart';

class ExerciseService {
  final String _baseUrl = dotenv.env['BACKEND_BASE_URL'] ?? "http://default-url.com";
  final AuthService _authService = AuthService();

  // Fetch all exercises
  Future<List<Exercise>> fetchAllExercises() async {
    final jwtToken = await _authService.getToken();
    if (jwtToken == null) {
      throw Exception("JWT token not found. User must log in.");
    }

    final response = await http.get(
      Uri.parse('$_baseUrl/exercises/search'),
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer $jwtToken',
      },
    );

    print("JWT Sent: $jwtToken");
    print("Response: ${response.statusCode} - ${response.body}");

    if (response.statusCode == 200) {
      final List<dynamic> data = json.decode(response.body);
      return data.map((json) => Exercise.fromJson(json)).toList();
    } else {
      throw Exception('Failed to load exercises');
    }
  }

  // Search exercises by name and/or primary muscle.
  // Both parameters are optional.
  Future<List<Exercise>> searchExercises({String? name, String? primaryMuscle}) async {
    final jwtToken = await _authService.getToken();
    if (jwtToken == null) {
      throw Exception("JWT token not found. User must log in.");
    }

    // Build query parameters dynamically.
    Map<String, String> queryParams = {};
    if (name != null && name.isNotEmpty) {
      queryParams['name'] = name;
    }
    if (primaryMuscle != null && primaryMuscle.isNotEmpty) {
      queryParams['primaryMuscle'] = primaryMuscle;
    }

    // If no parameter is provided, let the backend handle it (which returns a bad request).
    final uri = Uri.parse('$_baseUrl/exercises/search').replace(queryParameters: queryParams);

    final response = await http.get(
      uri,
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer $jwtToken',
      },
    );

    print("Search request sent to: $uri");
    print("Response: ${response.statusCode} - ${response.body}");

    if (response.statusCode == 200) {
      final List<dynamic> data = json.decode(response.body);
      return data.map((json) => Exercise.fromJson(json)).toList();
    } else {
      throw Exception('Failed to search exercises');
    }
  }
}
