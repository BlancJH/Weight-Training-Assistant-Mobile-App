import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:frontend_1/models/exercise.dart';
import 'package:frontend_1/widgets/exercise_preference_widget.dart';
import '../utils/design_utils.dart';
import 'package:frontend_1/widgets/shake_animation.dart';
import '../widgets/profile_avatar.dart';
import '../models/exercise_gif.dart';
import '../widgets/table_calendar.dart';
import '../widgets/gif_widget.dart';
import '../widgets/custome_list_view.dart';
import '../widgets/submit_button.dart';
import '../services/auth_service.dart';
import '../widgets/exercise_search_popup.dart';
import '../screens/workout_plan_screen.dart';
import '../services/exercise_plan_service.dart';
import '../utils/string_utils.dart';
import '../widgets/alert_widget.dart';
import '../widgets/delete_target.dart';
import '../services/exercise_service.dart';
import '../services/sphere_pack_service.dart';
import '../screens/unpack_reveal_screen.dart';

class CalendarPage extends StatefulWidget {
  final String? username;
  final String? profileUrl;

  const CalendarPage({this.username, this.profileUrl, Key? key}) : super(key: key);

  @override
  _CalendarPageState createState() => _CalendarPageState();
}

class _CalendarPageState extends State<CalendarPage> {
  final AuthService _authService = AuthService();
  final ExercisePlanService _exercisePlanService = ExercisePlanService();
  final ExerciseService _exerciseService = ExerciseService();
  final SpherePackService _spherePackService = SpherePackService();


  List<Map<String, dynamic>> exerciseData = [];
  DateTime _selectedDate = DateTime.now();
  bool isEditing = false;
  int? _planId;
  String? _jwtToken;

  @override
  void initState() {
    super.initState();
    _fetchExerciseData(date: _selectedDate);
    _fetchJwtToken();
  }

  // Fetch JWT
  Future<void> _fetchJwtToken() async {
    String? token = await _authService.getToken();
    setState(() {
      _jwtToken = token;
    });
  }

  // Helper function: Called when an exercise is selected from the search popup.
  void _onExerciseSelected(Exercise exercise) {
    print("Selected exercise: ${exercise.exerciseName}");
    setState(() {
      // Add the exercise as a new association.
      // For new exercises, leave 'workoutPlanExerciseId' as null.
      exerciseData.add({
        'workoutPlanExerciseId': null,
        'newExerciseId': exercise.id,
        'exerciseName': exercise.exerciseName,
        'gifUrl': exercise.gifUrl,
        'category': exercise.category,
        'primaryMuscle': exercise.primaryMuscle,
      });
    });
  }

  // When finishing edit mode, update the workout plan on the backend.
  Future<void> _updateWorkoutPlanExercises() async {
    if (_planId == null) {
      print("Plan ID is not available!");
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text("No workout plan found. Please create one first.")),
      );
      return;
    }

    // Build payload including all existing and new exercises
    final List<Map<String, dynamic>> payload = exerciseData.map((exerciseMap) {
      return {
        'workoutPlanExerciseId': exerciseMap['workoutPlanExerciseId'],  // existing exercises
        'newExerciseId': exerciseMap['newExerciseId'],                   // only new exercises have this
      };
    }).where((exercise) => 
      exercise['workoutPlanExerciseId'] != null || exercise['newExerciseId'] != null
    ).toList();

    if (payload.isEmpty) {
      print("No valid exercises to update.");
      return;
    }

    print("Sending update payload: ${jsonEncode(payload)}");

    try {
      await _exercisePlanService.updateWorkoutPlanExercises(_planId!, payload);
      print("Workout plan updated successfully.");
    } catch (error) {
      print("Failed to update workout plan: $error");
    }
  }

  // Show the search popup.
  void showExerciseSearchPopup(
    BuildContext context,
    Function(Exercise) onExerciseSelected,
  ) {
    showGeneralDialog(
      context: context,
      barrierDismissible: true,
      barrierLabel: MaterialLocalizations.of(context).modalBarrierDismissLabel,
      barrierColor: Colors.black.withOpacity(0.5),
      transitionDuration: const Duration(milliseconds: 200),
      pageBuilder: (BuildContext buildContext, Animation<double> animation, Animation<double> secondaryAnimation) {
        return Align(
          alignment: Alignment.topCenter,
          child: Container(
            margin: EdgeInsets.only(top: 100, left: 20, right: 20),
            width: MediaQuery.of(context).size.width * 0.9,
            height: 400,
            child: Material(
              borderRadius: BorderRadius.circular(16),
              child: ExerciseSearchPopup(
                onExerciseSelected: onExerciseSelected,
              ),
            ),
          ),
        );
      },
      transitionBuilder: (context, animation, secondaryAnimation, child) {
        return FadeTransition(
          opacity: animation,
          child: child,
        );
      },
    );
  }

  Future<void> _fetchExerciseData({DateTime? date}) async {
      try {
          final token = await _authService.getToken();
          if (token == null) {
              throw Exception('Token is null. Unable to fetch workout plans.');
          }
          final List<Map<String, dynamic>> fetchedPlans = await _exercisePlanService.fetchWorkoutPlans(date: date);
          if (fetchedPlans.isNotEmpty) {
              setState(() {
                  _planId = fetchedPlans.first['id'] as int;
                  exerciseData = (fetchedPlans.first['exercises'] as List<dynamic>)
                      .map((e) => {
                          'workoutPlanExerciseId': e['workoutPlanExerciseId'],
                          'exerciseId': e['exerciseId'],
                          'exerciseName': e['exerciseName'],
                          'gifUrl': e['gifUrl'],
                          'category': e['category'],
                          'primaryMuscle': e['primaryMuscle'],
                          'userPreference': e['userExercisePreference']
                      }).toList();
              });
          } else {
              setState(() {
                  _planId = null;
                  exerciseData = [];
              });
          }
      } catch (e) {
          print('Error fetching workout plans: $e');
          ScaffoldMessenger.of(context).showSnackBar(
              SnackBar(content: Text('Failed to fetch workout plans: $e')),
          );
      }
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context); 

    return Scaffold(
      body: Stack(
        children: [
          // Main content.
          Column(
            children: [
              Padding(
                padding: const EdgeInsets.all(16.0),
                child: CustomCalendar(
                  events: {},
                  onDaySelected: (selectedDay, events) {
                    print('Selected day: $selectedDay');
                    setState(() {
                      _selectedDate = selectedDay;
                    });
                    _fetchExerciseData(date: selectedDay);
                  },
                ),
              ),
              // Display the exercise list if available.
              exerciseData.isNotEmpty
                  ? Column(
                      children: [
                        Padding(
                          padding: const EdgeInsets.symmetric(horizontal: 16.0, vertical: 8.0),
                          child: Row(
                            mainAxisAlignment: MainAxisAlignment.spaceBetween,
                            children: [
                              Text(
                                'Your Exercises',
                                style: TextStyle(fontSize: 18.0, fontWeight: FontWeight.bold),
                              ),
                              Row(
                                mainAxisSize: MainAxisSize.min,
                                children: [
                                  // Re-plan button.
                                  IconButton(
                                    icon: Icon(Icons.refresh, color: buttonColor, size: 28),
                                    onPressed: () {
                                      AlertWidget.show(
                                        context: context,
                                        title: "Re-plan Workout",
                                        content: "Unfinished plans will be discarded.",
                                        onConfirm: () async {
                                          Navigator.push(
                                            context,
                                            MaterialPageRoute(
                                              builder: (context) => WorkoutPlanScreen(
                                                username: widget.username ?? 'Guest',
                                              ),
                                            ),
                                          );
                                        },
                                      );
                                    },
                                  ),
                                  // Pencil button toggles edit mode.
                                  IconButton(
                                    icon: Icon(Icons.edit, color: buttonColor, size: 28),
                                    onPressed: () async {
                                      if (isEditing) {
                                        // Finish editing and update the plan.
                                        await _updateWorkoutPlanExercises();
                                      }
                                      setState(() {
                                        isEditing = !isEditing;
                                      });
                                    },
                                  ),
                                ],
                              ),
                            ],
                          ),
                        ),
                        Container(
                          margin: const EdgeInsets.only(bottom: 16.0),
                          height: 170,
                          child: CustomListView(
                            // When editing, include an extra item for the "+" button.
                            itemCount: isEditing ? exerciseData.length + 1 : exerciseData.length,
                            scrollDirection: Axis.horizontal,
                            padding: const EdgeInsets.symmetric(horizontal: 16.0),
                            itemBuilder: (context, index) {
                              // In edit mode, if this is the extra item, show the "+" button.
                              if (isEditing && index == exerciseData.length) {
                                return Padding(
                                  padding: const EdgeInsets.symmetric(horizontal: 8.0),
                                  child: GestureDetector(
                                    onTap: () {
                                      showExerciseSearchPopup(context, _onExerciseSelected);
                                    },
                                    child: Container(
                                      width: 250,
                                      height: 150,
                                      decoration: BoxDecoration(
                                        color: primaryColor,
                                        borderRadius: BorderRadius.circular(8.0),
                                      ),
                                      child: Center(
                                        child: Icon(
                                          Icons.add,
                                          size: 50,
                                          color: highlightColor,
                                        ),
                                      ),
                                    ),
                                  ),
                                );
                              }

                              // Otherwise, build the exercise widget.
                              final gif = exerciseData[index];
                              final userPreference = gif['userPreference'] as String?;

                              Widget gifWidget = Stack(
                                children: [
                                  GifWidget(
                                    gifUrl: gif['gifUrl'] ??
                                        'https://media.giphy.com/media/3oEjI6SIIHBdRxXI40/giphy.gif',
                                    text: capitalise(gif['exerciseName'] ?? 'Exercise'),
                                    optionalText: gif['optionalText'] ??
                                        ((gif['sets'] != null && gif['reps'] != null)
                                            ? "${gif['sets']} sets ${gif['reps']} reps"
                                            : ''),
                                    width: 250,
                                    height: 150,
                                  ),
                                  Positioned(
                                    bottom: 16, // adjust the spacing from the bottom as needed
                                    right: 16,  // adjust the spacing from the right as needed
                                    child: SizedBox(
                                      width: 120, // adjust width as needed
                                      height: 40, // adjust height as needed
                                      child: ExercisePreferenceWidget(
                                        jwtToken: _jwtToken!, 
                                        exerciseId: gif['newExerciseId'] ?? gif['exerciseId'] ?? 0,
                                        initialPreference: userPreference,
                                      ),
                                    ),
                                  ),
                                ],
                              );

                              if (isEditing) {
                                Widget draggableWidget = Padding(
                                  padding: const EdgeInsets.symmetric(horizontal: 8.0),
                                  child: Draggable<Map<String, dynamic>>(
                                    data: gif,
                                    feedback: Material(
                                      color: Colors.transparent,
                                      child: Opacity(
                                        opacity: 0.8,
                                        child: gifWidget,
                                      ),
                                    ),
                                    childWhenDragging: Opacity(
                                      opacity: 0.5,
                                      child: gifWidget,
                                    ),
                                    child: gifWidget,
                                  ),
                                );
                                gifWidget = ShakeAnimation(
                                  shake: true,
                                  child: DragTarget<Map<String, dynamic>>(
                                    onWillAccept: (data) => data != null,
                                    onAccept: (data) {
                                      print("Dropped exercise: ${data['exerciseName']} on ${gif['exerciseName']}");
                                    },
                                    builder: (context, candidateData, rejectedData) {
                                      return draggableWidget;
                                    },
                                  ),
                                );
                                return gifWidget;
                              } else {
                                return Padding(
                                  padding: const EdgeInsets.symmetric(horizontal: 8.0),
                                  child: gifWidget,
                                );
                              }
                            },
                          ),
                        ),
                        Padding(
                          padding: const EdgeInsets.symmetric(horizontal: 16.0),
                          child: SubmitButton(
                            text: 'Workout Completed!',
                            onPressed: () async {
                              try {
                                // Call the API with "BRONZE" as the pack type.
                                final sphereList = await _spherePackService.generateAndSaveSpherePack(packType: "BRONZE");
                                
                                // Navigate to the reveal screen with the returned sphere list.
                                Navigator.push(
                                  context,
                                  MaterialPageRoute(
                                    builder: (context) => UnpackRevealScreen(spheres: sphereList),
                                  ),
                                );
                              } catch (e) {
                                ScaffoldMessenger.of(context).showSnackBar(
                                  SnackBar(content: Text("Failed to complete workout: $e")),
                                );
                              }
                            },
                          ),
                        ),
                      ],
                    )
                  : Center(
                      child: Column(
                        mainAxisSize: MainAxisSize.min,
                        children: [
                          Text(
                            'No exercises available.',
                            style: TextStyle(fontSize: 16.0, color: Colors.grey),
                          ),
                          SizedBox(height: 16.0),
                          SubmitButton(
                            text: 'Plan Workout',
                            onPressed: () {
                              Navigator.push(
                                context,
                                MaterialPageRoute(
                                  builder: (context) => WorkoutPlanScreen(
                                    username: widget.username ?? 'Guest',
                                  ),
                                ),
                              );
                            },
                          ),
                        ],
                      ),
                    ),
            ],
          ),
          // DeleteTarget widget shown only when in editing mode.
          if (isEditing)
            Positioned(
              bottom: 30,
              left: 0,
              right: 0,
              child: DeleteTarget(
                onAccept: (data) {
                  setState(() {
                    exerciseData.remove(data);
                  });
                },
              ),
            ),
        ],
      ),
    );
  }
}
