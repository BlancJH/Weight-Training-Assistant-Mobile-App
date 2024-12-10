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
              labelText: 'User Name',
               border: OutlineInputBorder(),
              hintText: 'Enter your user name',
            ),
            keyboardType: TextInputType.name,
            textCapitalization: TextCapitalization.words,
            autocorrect: false, // Disables autocorrect or suggestions
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
            SizedBox(height: 16.0),
            TextField( // Phone number field
              decoration: InputDecoration( 
                labelText: 'Mobile Phone',
                border: OutlineInputBorder(),
                hintText: 'Enter your phone number',
              ),
              keyboardType: TextInputType.phone, // Optimised keyboard for number
              autocorrect: false,
            ),            
          ],
        ),
      ),
    );
  }
}
