import 'package:flutter/material.dart';
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

  @override
  void initState() {
    super.initState();
    _fetchUserData();
    _fetchExerciseData();
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

  Future<void> _fetchExerciseData() async {
    try {
      final token = await _authService.getToken(); // Get JWT token

      if (token == null) {
        throw Exception('Token is null. Unable to fetch workout plans.');
      }

      final fetchedPlans = await _exercisePlanService.fetchWorkoutPlans();
      setState(() {
        exerciseData = fetchedPlans;
      });
      print("Fetched workout plans: $exerciseData");
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
                print('Events: $events');
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
                          TextButton(
                            onPressed: () {
                              print('Re-plan button pressed!');
                            },
                            child: Text(
                              'Re-plan >',
                              style: TextStyle(
                                fontSize: 16.0,
                                color: Colors.blue,
                              ),
                            ),
                          ),
                        ],
                      ),
                    ),
                    Container(
                      margin: const EdgeInsets.only(bottom: 16.0),
                      height: 170,
                      child: CustomListView(
                        itemCount: exerciseData.length,
                        scrollDirection: Axis.horizontal,
                        padding:
                            const EdgeInsets.symmetric(horizontal: 16.0),
                        itemBuilder: (context, index) {
                          final gif = exerciseData[index];

                          // Ensure correct keys exist in gif object
                          return Padding(
                            padding:
                                const EdgeInsets.symmetric(horizontal: 8.0),
                            child: GifWidget(
                              gifUrl: gif['gifUrl'] ?? '', // Ensure key matches backend response
                              text: gif['text'] ?? 'Exercise', 
                              optionalText: gif['optionalText'] ?? '',
                              width: 200,
                              height: 150,
                            ),
                          );
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
                  child: Text(
                    'No exercises available.',
                    style: TextStyle(fontSize: 16.0, color: Colors.grey),
                  ),
                ),

          SizedBox(height: 16.0),

          Padding(
            padding: const EdgeInsets.symmetric(horizontal: 16.0),
            child: SubmitButton(
              text: 'Plan Workout',
              onPressed: () {
                Navigator.push(
                  context,
                  MaterialPageRoute(
                      builder: (context) =>
                          WorkoutPlanScreen(username: username ?? 'Guest')),
                );
              },
            ),
          ),
        ],
      ),
    );
  }
}
