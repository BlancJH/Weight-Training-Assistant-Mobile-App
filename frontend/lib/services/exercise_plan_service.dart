import 'dart:convert';
import 'package:http/http.dart' as http;

class ExercisePlanService {
  final String baseUrl = 'http://localhost:8080';

  Future<String> sendUserDetails(Map<String, dynamic> userDetails) async {
    final response = await http.post(
      Uri.parse('$baseUrl/api/v1/workokut-plan/generate'),
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode(userDetails),
    );

    if (response.statusCode == 200) {
      return response.body;
    } else {
      throw Exception('Failed to process request: ${response.reasonPhrase}');
    }
  }
}
