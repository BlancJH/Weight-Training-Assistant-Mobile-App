import 'dart:convert';
import 'package:http/http.dart' as http;
import 'package:flutter_dotenv/flutter_dotenv.dart';

class ExercisePlanService {
  final String? _baseUrl = dotenv.env['BACKEND_BASE_URL']?? "http://default-url.com";

  Future<String> sendUserDetails(Map<String, dynamic> userDetails) async {
    final response = await http.post(
      Uri.parse('$_baseUrl/v1/workokut-plan/generate'),
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode(userDetails),
    );

    if (response.statusCode == 200) {
      return response.body;
    } else {
      throw Exception('Failed to process request: ${response.reasonPhrase}');
    }
  }

  Future<List<Map<String, dynamic>>> fetchWorkoutPlans(String token) async {
    final response = await http.get(
      Uri.parse('$_baseUrl/v1/workout-plan/get'),
      headers: {
        'Authorization': 'Bearer $token',
        'Content-Type': 'application/json',
      },
    );

    if (response.statusCode == 200) {
      return List<Map<String, dynamic>>.from(jsonDecode(response.body));
    } else {
      throw Exception('Failed to fetch workout plans: ${response.reasonPhrase}');
    }
  }

}
