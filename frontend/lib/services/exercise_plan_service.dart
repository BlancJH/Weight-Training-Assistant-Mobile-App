import 'dart:convert';
import 'package:http/http.dart' as http;
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'auth_service.dart';

class ExercisePlanService {
  final String? _baseUrl = dotenv.env['BACKEND_BASE_URL']?? "http://default-url.com";
  final AuthService _authService = AuthService();

  Future<String> createWorkoutPlans() async {
    final jwtToken = await _authService.getToken();

    if (jwtToken == null) {
      throw Exception("JWT token not found. User must log in.");
    }

    final response = await http.post(
      Uri.parse('$_baseUrl/v1/workout-plans/generate'),
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer $jwtToken',
      },
      // Pass an empty JSON object if the endpoint requires a body.
      body: jsonEncode({}),
    );

    print("JWT Sent: $jwtToken");
    print("API Response: ${response.statusCode} - ${response.body}");

    if (response.statusCode == 200) {
      return response.body;
    } else {
      throw Exception('Failed to process request: ${response.reasonPhrase}');
    }
  }

  Future<List<Map<String, dynamic>>> fetchWorkoutPlans() async {
    final jwtToken = await _authService.getToken();

    if (jwtToken == null) {
      throw Exception("JWT token not found. Cannot fetch workout plans.");
    }

    final response = await http.get(
      Uri.parse('$_baseUrl/v1/workout-plans/get'),
      headers: {
        'Authorization': 'Bearer $jwtToken',
        'Content-Type': 'application/json',
      },
    );

    print("Fetching Workout Plans: ${response.statusCode} - ${response.body}");

    if (response.statusCode == 200) {
      final List<dynamic> jsonResponse = jsonDecode(response.body);

      return jsonResponse.map((plan) => plan as Map<String, dynamic>).toList();
    } else {
      throw Exception('Failed to fetch workout plans: ${response.reasonPhrase}');
    }
  }
}
