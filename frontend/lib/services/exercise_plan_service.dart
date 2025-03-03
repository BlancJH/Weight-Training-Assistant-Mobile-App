import 'dart:convert';
import 'package:http/http.dart' as http;
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:intl/intl.dart';
import 'auth_service.dart';

class ExercisePlanService {
  final String? _baseUrl = dotenv.env['BACKEND_BASE_URL']?? "http://default-url.com";
  final AuthService _authService = AuthService();

  Future<Map<String, dynamic>> createWorkoutPlans() async {
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
      body: jsonEncode({}), // adjust the payload as needed
    );

    print("JWT Sent: $jwtToken");
    print("Response: ${response.statusCode} - ${response.body}");

    if (response.statusCode == 200) {
      // Parse the JSON response.
      return jsonDecode(response.body) as Map<String, dynamic>;
    } else {
      throw Exception('Failed to generate workout plan: ${response.reasonPhrase}');
    }
  }

  /// If [date] is provided, it is sent as a query parameter in the format yyyy-MM-dd.
  Future<List<Map<String, dynamic>>> fetchWorkoutPlans({DateTime? date}) async {
    final jwtToken = await _authService.getToken();

    if (jwtToken == null) {
      throw Exception("JWT token not found. Cannot fetch workout plans.");
    }

    // Build the URL, appending the date query parameter if provided.
    String url = '$_baseUrl/v1/workout-plans/get';
    if (date != null) {
      final formattedDate = DateFormat('yyyy-MM-dd').format(date);
      url += '?date=$formattedDate';
    }

    final response = await http.get(
      Uri.parse(url),
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

  Future<void> updateWorkoutPlanExercises(int planId, List<Map<String, dynamic>> updatedExercises) async {
    final jwtToken = await _authService.getToken();
    if (jwtToken == null) {
      throw Exception("JWT token not found. User must log in.");
    }
    
    final url = Uri.parse('$_baseUrl/v1/workout-plans/$planId/edit');
    final response = await http.put(
      url,
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer $jwtToken',
      },
      body: jsonEncode(updatedExercises), // Send the list of maps.
    );
    
    if (response.statusCode != 200) {
      throw Exception('Failed to update workout plan: ${response.body}');
    }
  }

}
