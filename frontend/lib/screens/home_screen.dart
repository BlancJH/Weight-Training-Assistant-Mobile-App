import 'package:flutter/material.dart';
import '../widgets/profile_avatar.dart';
import '../widgets/table_calendar.dart';

class HomeScreen extends StatelessWidget {
  final Map<DateTime, List<String>> _events = {};

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.white, // Blank white screen
      appBar: AppBar(
        backgroundColor: Colors.white,
        elevation: 0, // Remove shadow
        actions: [
          Padding(
            padding: const EdgeInsets.only(right: 16.0, top: 8.0),
            child: ProfileAvatar(
              username: 'John Doe', // Example name, requires to be replaced to the actual
              imageUrl: '', // requires to be replaced to the actual
              onTap: () {
                print('Profile tapped!'); // Function need to show floating box
              },
            ),
          ),
        ],
      ),
      body: Column(
        children: [
          // Use the CustomCalendar widget
          Padding(
            padding: const EdgeInsets.all(16.0),
            child: CustomCalendar(
              events: _events, // Pass events to the widget
              onDaySelected: (selectedDay, events) {
                print('Selected day: $selectedDay');
                print('Events: $events');
              },
            ),
          ),
          Expanded(
            child: Center(
              child: Text(
                'Main Content Area',
                style: TextStyle(fontSize: 18.0),
              ),
            ),
          ),
        ],
      ),
    );
  }
}