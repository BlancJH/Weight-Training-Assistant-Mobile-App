import 'package:flutter/material.dart';
import 'package:frontend_1/models/exercise.dart';
import 'package:frontend_1/widgets/shake_animation.dart';
import '../widgets/profile_avatar.dart';
import '../widgets/table_calendar.dart';
import '../widgets/gif_widget.dart';
import '../widgets/custome_list_view.dart';
import '../widgets/submit_button.dart';
import '../services/auth_service.dart';
import '../models/exercise_gif.dart';
import '../widgets/exercise_search_popup.dart';
import '../widgets/popup_menu.dart';
import '../screens/profile_screen.dart';
import '../screens/workout_plan_screen.dart';
import '../services/exercise_plan_service.dart';
import '../utils/string_utils.dart';
import '../widgets/alert_widget.dart';
import '../widgets/delete_target.dart';
import '../services/exercise_service.dart';

class HomeScreen extends StatefulWidget {
  @override
  _HomeScreenState createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  final AuthService _authService = AuthService();
  final ExercisePlanService _exercisePlanService = ExercisePlanService();
  final ExerciseService _exerciseService = ExerciseService();
  String? username;
  String? profileUrl;
  List<Map<String, dynamic>> exerciseData = [];

  // to store the selected date from the calendar
  DateTime _selectedDate = DateTime.now();

  // workoutPlan edit mode
  bool isEditing = false;

  @override
  void initState() {
    super.initState();
    _fetchUserData();
    _fetchExerciseData(date: _selectedDate);
  }

  int? _planId; 

  Future<void> _updateWorkoutPlanExercises() async {
    if (_planId == null) {
      print("Plan ID is not available!");
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text("No workout plan found. Please create one first.")),
      );
      return;
    }
    try {
      await _exercisePlanService.updateWorkoutPlanExercises(_planId!, exerciseData);
      print("Workout plan updated successfully with: $exerciseData");
    } catch (error) {
      print("Failed to update workout plan: $error");
    }
  }

  // Show the search popup. No preloaded exercises are passed.
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

  Future<void> _fetchUserData() async {
    try {
      final userData = await _authService.decodeToken();
      setState(() {
        username = userData?['username'] ?? 'Guest';
        profileUrl = userData?['profileUrl'];
      });
    } catch (e) {
      print('Error fetching user data: $e');
      setState(() {
        username = 'Guest';
        profileUrl = null;
      });
    }
  }

  Future<void> _fetchExerciseData({DateTime? date}) async {
    try {
      final token = await _authService.getToken();
      if (token == null) {
        throw Exception('Token is null. Unable to fetch workout plans.');
      }

      // Pass the date parameter to the service method.
      final List<Map<String, dynamic>> fetchedPlans = 
          await _exercisePlanService.fetchWorkoutPlans(date: date);

      setState(() {
        exerciseData = fetchedPlans
            .map((plan) => plan['exercises'] as List<dynamic>)
            .expand((exerciseList) => exerciseList.map((e) => e as Map<String, dynamic>))
            .toList();
      });

      print("Fetched Exercises: $exerciseData");
    } catch (e) {
      print('Error fetching workout plans: $e');
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('Failed to fetch workout plans: $e')),
      );
    }
  }

  void _handleMenuSelection(MenuOptions option) async {
    switch (option) {
      case MenuOptions.profile:
        Navigator.push(
          context,
          MaterialPageRoute(
            builder: (context) => ProfileScreen(
              profileImageUrl: profileUrl ?? '',
              username: username ?? 'Guest',
            ),
          ),
        );
        break;
      case MenuOptions.settings:
        print('Settings tapped!');
        break;
      case MenuOptions.logout:
        await _authService.logoutUser();
        Navigator.pushReplacementNamed(context, '/login');
        break;
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        backgroundColor: Colors.white,
        elevation: 0,
        actions: [
          Padding(
            padding: const EdgeInsets.only(right: 16.0, top: 8.0),
            child: PopupMenuWidget(
              trigger: ProfileAvatar(
                username: username ?? 'Loading...',
                imageUrl: profileUrl ?? '',
              ),
              onSelected: _handleMenuSelection,
            ),
          ),
        ],
      ),
      body: Stack(
        children: [
          // Main content as a Column.
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
              // Conditional rendering based on exercise data.
              exerciseData.isNotEmpty
                  ? Column(
                      children: [
                        Padding(
                          padding: const EdgeInsets.symmetric(
                              horizontal: 16.0, vertical: 8.0),
                          child: Row(
                            mainAxisAlignment: MainAxisAlignment.spaceBetween,
                            children: [
                              Text(
                                'Your Exercises',
                                style: TextStyle(
                                  fontSize: 18.0,
                                  fontWeight: FontWeight.bold,
                                ),
                              ),
                              Row(
                                mainAxisSize: MainAxisSize.min,
                                children: [
                                  // Re-plan button.
                                  IconButton(
                                    icon: Icon(Icons.refresh,
                                        color: Colors.blue, size: 28),
                                    onPressed: () {
                                      AlertWidget.show(
                                        context: context,
                                        title: "Re-plan Workout",
                                        content:
                                            "Unfinished plans will be discarded.",
                                        onConfirm: () async {
                                          Navigator.push(
                                            context,
                                            MaterialPageRoute(
                                              builder: (context) =>
                                                  WorkoutPlanScreen(
                                                      username:
                                                          username ?? 'Guest'),
                                            ),
                                          );
                                        },
                                      );
                                    },
                                  ),
                                  // Pencil button now toggles edit mode.
                                  IconButton(
                                    icon: Icon(Icons.edit,
                                        color: Colors.blue, size: 28),
                                    onPressed: () async {
                                      // If currently editing, finish editing mode and update workout plan.
                                      if (isEditing) {
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
                            // In edit mode, include an extra item for the "+" button.
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
                                      // Call the search popup to add an exercise.
                                      showExerciseSearchPopup(context, (exercise) {
                                        print("Selected exercise: ${exercise.exerciseName}");
                                        setState(() {
                                          // Add the selected exercise to the list.
                                          exerciseData.add({
                                            'workoutPlanExerciseId': null, // New exercise; no existing association.
                                            'newExerciseId': exercise.id,    // Use 'newExerciseId' here.
                                            'exerciseName': exercise.exerciseName,
                                            'gifUrl': exercise.gifUrl,
                                            'category': exercise.category,
                                            'primaryMuscle': exercise.primaryMuscle,
                                            // Add additional fields if needed.
                                          });
                                        });
                                        Navigator.of(context, rootNavigator: true).pop();
                                      });
                                    },
                                    child: Container(
                                      width: 250,
                                      height: 150,
                                      decoration: BoxDecoration(
                                        color: Colors.grey[300],
                                        borderRadius: BorderRadius.circular(8.0),
                                      ),
                                      child: Center(
                                        child: Icon(
                                          Icons.add,
                                          size: 50,
                                          color: Colors.blue,
                                        ),
                                      ),
                                    ),
                                  ),
                                );
                              }

                              // Otherwise, build the exercise widget.
                              final gif = exerciseData[index];
                              // Build GifWidget.
                              Widget gifWidget = GifWidget(
                                gifUrl: gif['gifUrl'] ??
                                    'https://media.giphy.com/media/3oEjI6SIIHBdRxXI40/giphy.gif',
                                text: capitalise(gif['exerciseName'] ?? 'Exercise'),
                                optionalText: gif['optionalText'] ??
                                    ((gif['sets'] != null && gif['reps'] != null)
                                        ? "${gif['sets']} sets ${gif['reps']} reps"
                                        : ''),
                                width: 250,
                                height: 150,
                              );

                              if (isEditing) {
                                // Wrap the widget in a Draggable and DragTarget for reordering if needed.
                                Widget draggableWidget = Padding(
                                  padding: const EdgeInsets.symmetric(horizontal: 8.0),
                                  child: Draggable<Map<String, dynamic>>(
                                    data: gif, // exercise data
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

                              // Wrap the draggable widget in a DragTarget.
                                gifWidget = ShakeAnimation(
                                  shake: true,
                                  child: DragTarget<Map<String, dynamic>>(
                                    onWillAccept: (data) => data != null,
                                    onAccept: (data) {
                                      print("Dropped exercise: ${data['exerciseName']} on ${gif['exerciseName']}");
                                      // TODO: Add your logic to update the list (e.g., reordering).
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
                            text: 'Start workout!',
                            onPressed: () {
                              print('Workout button pressed!');
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
                                      username: username ?? 'Guest'),
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
