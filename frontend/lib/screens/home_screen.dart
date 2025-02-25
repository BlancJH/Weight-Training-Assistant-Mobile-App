import 'package:flutter/material.dart';
import 'package:frontend_1/widgets/shake_animation.dart';
import '../widgets/profile_avatar.dart';
import '../widgets/table_calendar.dart';
import '../widgets/gif_widget.dart';
import '../widgets/custome_list_view.dart';
import '../widgets/submit_button.dart';
import '../services/auth_service.dart';
import '../models/exercise_gif.dart';
import '../widgets/popup_menu.dart';
import '../screens/profile_screen.dart';
import '../screens/workout_plan_screen.dart';
import '../services/exercise_plan_service.dart';
import '../utils/string_utils.dart';
import '../widgets/alert_widget.dart';
import '../services/exercise_plan_service.dart';

class HomeScreen extends StatefulWidget {
  @override
  _HomeScreenState createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  final AuthService _authService = AuthService();
  final ExercisePlanService _exercisePlanService = ExercisePlanService();
  String? username;
  String? profileUrl;
  List<Map<String, dynamic>> exerciseData = []; // Non-nullable

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

      // Pass the date parameter to the service method
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
      body: Column(
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

          // Conditional rendering based on exercise data
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
                                IconButton(
                                  icon: Icon(Icons.refresh, color: Colors.blue, size: 28),
                                  onPressed: () {
                                    AlertWidget.show(
                                      context: context,
                                      title: "Re-plan Workout",
                                      content: "Unfinished plans will be discarded.",
                                      onConfirm: () async {
                                        Navigator.push(
                                          context,
                                          MaterialPageRoute(
                                            builder: (context) => WorkoutPlanScreen(username: username ?? 'Guest'),
                                          ),
                                        );
                                      },
                                    );
                                  },
                                ),
                                IconButton(
                                  icon: Icon(Icons.edit, color: Colors.blue, size: 28),
                                  onPressed: () {
                                    setState(() {
                                      isEditing = !isEditing;
                                    });
                                  },
                                ),
                              ],
                            ),
                          ],
                        )
                    ),
                    Container(
                      margin: const EdgeInsets.only(bottom: 16.0),
                      height: 170,
                      child: CustomListView(
                        itemCount: exerciseData.length,
                        scrollDirection: Axis.horizontal,
                        padding: const EdgeInsets.symmetric(horizontal: 16.0),
                        itemBuilder: (context, index) {
                          final gif = exerciseData[index];
                          
                          // Build GifWidget.
                          Widget gifWidget = GifWidget(
                            gifUrl: gif['gifUrl'] ?? 'https://media.giphy.com/media/3oEjI6SIIHBdRxXI40/giphy.gif',
                            text: capitalise(gif['exerciseName'] ?? 'Exercise'),
                            optionalText: gif['optionalText'] ??
                                ((gif['sets'] != null && gif['reps'] != null)
                                    ? "${gif['sets']} sets ${gif['reps']} reps"
                                    : ''),
                            width: 250,
                            height: 150,
                          );

                          // If in editing mode, wrap with ShakeAnimation and Draggable.
                          if (isEditing) {
                            gifWidget = ShakeAnimation(
                              shake: true,
                              child: gifWidget,
                            );
                            
                            gifWidget = Padding(
                              padding: const EdgeInsets.symmetric(horizontal: 8.0),
                              child: Draggable<Map<String, dynamic>>(
                                data: gif, // Pass the exercise data for deletion/edit actions.
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
                            return gifWidget;
                          } else {
                            // Otherwise, display normally.
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
                    mainAxisSize: MainAxisSize.min, // Ensures the Column only takes as much space as needed
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
                              builder: (context) =>
                                  WorkoutPlanScreen(username: username ?? 'Guest'),
                            ),
                          );
                        },
                      ),
                    ],
                  ),
                ),
        ],
      ),
    );
  }
}
