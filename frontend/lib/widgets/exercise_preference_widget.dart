import 'package:flutter/material.dart';
import '../services/exercise_preference_service.dart';

class ExercisePreferenceWidget extends StatefulWidget {
  final String jwtToken;
  final int exerciseId;

  const ExercisePreferenceWidget({
    Key? key,
    required this.jwtToken,
    required this.exerciseId,
  }) : super(key: key);

  @override
  _ExercisePreferenceWidgetState createState() => _ExercisePreferenceWidgetState();
}

class _ExercisePreferenceWidgetState extends State<ExercisePreferenceWidget> {
  final ExercisePreferenceService _preferenceService = ExercisePreferenceService();

  bool isFavorite = false;
  bool isDisliked = false;
  String? selectedDislikeReason;

  /// **Mapping of shown values to actual values**
  final Map<String, String> dislikeReasons = {
    "Too difficult": "DIFFICULT",
    "Not effective": "EASY",
    "Risk of injury": "INJURY",
    "No equipment": "NO_EQUIPMENT",
    "Others": "ETC",
  };

  void _updatePreference(bool favorite, bool dislike, [String? dislikeReason]) async {
    bool success = await _preferenceService.updatePreference(
      jwtToken: widget.jwtToken,
      exerciseId: widget.exerciseId,
      favorite: favorite,
      dislike: dislike,
      dislikeReason: dislikeReason,
    );

    if (success) {
      setState(() {
        isFavorite = favorite;
        isDisliked = dislike;
        selectedDislikeReason = dislikeReason;
      });
    }
  }

  void _onFavoritePressed() {
    _updatePreference(true, false);
  }

  void _onDislikePressed() {
    _showDislikeReasonDialog();
  }

  void _showDislikeReasonDialog() {
    showDialog(
      context: context,
      builder: (context) {
        return AlertDialog(
          title: const Text("Select Dislike Reason"),
          content: Column(
            mainAxisSize: MainAxisSize.min,
            children: dislikeReasons.entries.map((entry) {
              return ListTile(
                title: Text(entry.key), // Show the user-friendly label
                leading: Radio<String>(
                  value: entry.value, // Store the actual value
                  groupValue: selectedDislikeReason,
                  onChanged: (value) {
                    Navigator.pop(context);
                    _updatePreference(false, true, value);
                  },
                ),
              );
            }).toList(),
          ),
        );
      },
    );
  }

  @override
  Widget build(BuildContext context) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.center,
      children: [
        // Favorite Button
        IconButton(
          icon: Icon(
            Icons.favorite,
            color: isFavorite ? Colors.red : Colors.grey,
            size: 32,
          ),
          onPressed: _onFavoritePressed,
        ),

        const SizedBox(width: 16),

        // Dislike Button
        IconButton(
          icon: Icon(
            Icons.thumb_down,
            color: isDisliked ? Colors.blueGrey : Colors.grey,
            size: 32,
          ),
          onPressed: _onDislikePressed,
        ),
      ],
    );
  }
}
