import 'package:flutter/material.dart';

class RegisterScreen extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Register'),
      ),
      body: Padding(
        padding: EdgeInsets.all(20.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            TextField(decoration: InputDecoration( //First Name field
              labelText: 'First Name',
               border: OutlineInputBorder(),
              hintText: 'Enter your First Name',
            ),
            keyboardType: TextInputType.name,
            textCapitalization: TextCapitalization.words,
            autocorrect: false, // Disables autocorrect or suggestions
            ),
            SizedBox(height: 16.0),
            TextField( // Last Name field
              decoration: InputDecoration(
                labelText: 'Last Name',
                border: OutlineInputBorder(),
                hintText: 'Enter your Last Name',
              ),
              keyboardType: TextInputType.name,
              autocorrect: false,
            ),
            SizedBox(height: 16.0),
            TextField( // Email field
              decoration: InputDecoration( 
                labelText: 'Email',
                border: OutlineInputBorder(),
                hintText: 'Enter your email',
              ),
              keyboardType: TextInputType.emailAddress, // Optimised keyboard for email
              autocorrect: false,
            ),
          ],
        ),
      ),
    );
  }
}
