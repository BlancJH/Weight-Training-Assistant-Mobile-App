import 'dart:convert';
import 'package:http/http.dart' as http;
import 'package:flutter_dotenv/flutter_dotenv.dart';

class ExercisePreferenceService {
  final String? _baseUrl = dotenv.env['BACKEND_BASE_URL']?? "http://default-url.com";

  Future<bool> updatePreference({
    required String jwtToken,
    required int exerciseId,
    required bool favorite,
    required bool dislike,
    String? dislikeReason,
  }) async {
    final Map<String, dynamic> requestData = {
      'exerciseId': exerciseId,
      'favorite': favorite,
      'dislike': dislike,
      'dislikeReason': dislikeReason,
    };

    try {
      final response = await http.post(
        Uri.parse(_baseUrl!),
        headers: {
          'Content-Type': 'application/json',
          'Authorization': 'Bearer $jwtToken',
        },
        body: jsonEncode(requestData),
      );

      if (response.statusCode == 200) {
        print("Preference updated successfully: ${response.body}");
        return true;
      } else {
        print("Failed to update preference: ${response.body}");
        return false;
      }
    } catch (e) {
      print("Error updating preference: $e");
      return false;
    }
  }
}
