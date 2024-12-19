import 'package:flutter/material.dart';

class HomeScreen extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Home'),
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
