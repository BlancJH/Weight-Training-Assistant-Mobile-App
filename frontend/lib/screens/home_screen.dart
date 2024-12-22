// ignore_for_file: prefer_const_constructors

import 'package:flutter/material.dart';
import 'package:frontend_1/models/exercise_gif.dart';
import '../widgets/profile_avatar.dart';
import '../widgets/table_calendar.dart';
import '../widgets/gif_widget.dart';
import '../widgets/custome_list_view.dart';
import '../widgets/submit_button.dart';

class HomeScreen extends StatelessWidget {
  final Map<DateTime, List<String>> _events = {};
    // Mock data
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
  Widget build(BuildContext context) {
    // Build widgets from mock data
    final gifWidgets = mockData.map((gif) {
      return GifWidget(
        gifUrl: gif.gifUrl,
        text: gif.text,
        optionalText: gif.optionalText,
        width: 300, // Set custom width
        height: 150, // Set custom height
      );
    }).toList();

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

          // Horizontal list of GIF widgets and Submit Button
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
                margin: const EdgeInsets.only(bottom: 16.0), // Add spacing below the list
                height: 170, // Increased height to accommodate optional text
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
                        width: 200, // Adjust width as needed
                        height: 150, // Adjust height as needed
                      ),
                    );
                  },
                  emptyWidget: Center(
                    child: Text('No GIFs available'),
                  ),
                ),
              ),
              
              // Start workout button
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

          // Remaining space content
          Expanded(
            child: Center(
              child: Text(
                'Additional content here',
                style: TextStyle(fontSize: 16.0, color: Colors.grey),
              ),
            ),
          ),
        ],
      ),
    );
  }
}