import 'package:flutter/material.dart';
import '../widgets/profile_avatar.dart';

class HomeScreen extends StatelessWidget {
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
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Form(
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              // Example TextField
              TextFormField(
                decoration: InputDecoration(
                  labelText: 'Placeholder Field',
                  border: OutlineInputBorder(),
                ),
              ),
              SizedBox(height: 16.0),

              // Example Submit Button
              ElevatedButton(
                onPressed: () {
                  // Do nothing for now
                  print('Submit button pressed');
                },
                child: Text('Submit'),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
