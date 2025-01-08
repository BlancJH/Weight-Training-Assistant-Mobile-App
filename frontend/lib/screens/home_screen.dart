import 'package:flutter/material.dart';
import '../widgets/profile_avatar.dart';
import '../widgets/table_calendar.dart';
import '../widgets/gif_widget.dart';
import '../widgets/custome_list_view.dart';
import '../widgets/submit_button.dart';
import '../services/auth_service.dart';
import '../models/exercise_gif.dart';

class HomeScreen extends StatefulWidget {
  @override
  _HomeScreenState createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  final AuthService _authService = AuthService();
  String? username;
  String? profileUrl;

  final List<ExerciseGifModel> mockData = [
    ExerciseGifModel(
      gifUrl: 'https://media.giphy.com/media/3oEjI6SIIHBdRxXI40/giphy.gif',
      text: 'This is the first GIF description.',
      optionalText: 'Optional note for the first GIF.',
    ),
    ExerciseGifModel(
      gifUrl: 'https://media.giphy.com/media/l0HlOvJ7yaacpuSas/giphy.gif',
      text: 'Second GIF description.',
    ),
    ExerciseGifModel(
      gifUrl: 'https://media.giphy.com/media/26xBRBfwnZFWFuGiM/giphy.gif',
      text: 'Third GIF with no optional text.',
    ),
    ExerciseGifModel(
      gifUrl: 'https://media.giphy.com/media/26xBRBfwnZFWFuGiM/giphy.gif',
      text: 'Fourth GIF with no optional text.',
    ),
  ];

  @override
  void initState() {
    super.initState();
    _fetchUserData();
  }

  Future<void> _fetchUserData() async {
    try {
      final userData = await _authService.decodeToken(); // Adjusted to use decodeToken
      setState(() {
        username = userData?['username'] ?? 'Guest'; // Default to 'Guest'
        profileUrl = userData?['profileUrl']; // Default to null if missing
      });
    } catch (e) {
      print('Error fetching user data: $e');
      setState(() {
        username = 'Guest';
        profileUrl = null;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.white,
      appBar: AppBar(
        backgroundColor: Colors.white,
        elevation: 0,
        actions: [
          Padding(
            padding: const EdgeInsets.only(right: 16.0, top: 8.0),
            child: ProfileAvatar(
              username: username ?? 'Loading...', // Show username
              imageUrl: profileUrl ?? '', // Show profile URL or fallback
              onTap: () {
                print('Profile tapped!');
              },
            ),
          ),
        ],
      ),
      body: Column(
        children: [
          Padding(
            padding: const EdgeInsets.all(16.0),
            child: CustomCalendar(
              events: {}, // Replace with your event data
              onDaySelected: (selectedDay, events) {
                print('Selected day: $selectedDay');
                print('Events: $events');
              },
            ),
          ),
          Column(
            children: [
              Padding(
                padding: const EdgeInsets.symmetric(horizontal: 16.0, vertical: 8.0),
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
                  itemCount: mockData.length,
                  scrollDirection: Axis.horizontal,
                  padding: const EdgeInsets.symmetric(horizontal: 16.0),
                  itemBuilder: (context, index) {
                    final gif = mockData[index];
                    return Padding(
                      padding: const EdgeInsets.symmetric(horizontal: 8.0),
                      child: GifWidget(
                        gifUrl: gif.gifUrl,
                        text: gif.text,
                        optionalText: gif.optionalText,
                        width: 200,
                        height: 150,
                      ),
                    );
                  },
                  emptyWidget: Center(
                    child: Text('No GIFs available'),
                  ),
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
          ),
        ],
      ),
    );
  }
}
