import 'package:flutter/material.dart';
import '../widgets/profile_avatar.dart';
import '../widgets/custom_text_field.dart';
import '../widgets/submit_button.dart';

class ProfileScreen extends StatefulWidget {
  final String profileImageUrl;
  final String username;

  const ProfileScreen({
    required this.profileImageUrl,
    required this.username,
    Key? key,
  }) : super(key: key);

  @override
  State<ProfileScreen> createState() => _ProfileScreenState();
}

class _ProfileScreenState extends State<ProfileScreen> {
  // Controllers for text fields
  final TextEditingController birthdayController = TextEditingController();
  final TextEditingController heightController = TextEditingController();
  final TextEditingController weightController = TextEditingController();
  final TextEditingController genderController = TextEditingController();
  final TextEditingController constraintsController = TextEditingController();
  final TextEditingController workoutPurposeController = TextEditingController();

  // Function to show the date picker
  Future<void> _selectBirthday() async {
    DateTime? pickedDate = await showDatePicker(
      context: context,
      initialDate: DateTime(2000),
      firstDate: DateTime(1900),
      lastDate: DateTime.now(),
    );

    if (pickedDate != null) {
      setState(() {
        birthdayController.text = "${pickedDate.year}-${pickedDate.month}-${pickedDate.day}";
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Profile'),
        backgroundColor: Colors.white,
        elevation: 0,
        iconTheme: const IconThemeData(color: Colors.black),
        centerTitle: true,
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          children: [
            const SizedBox(height: 20),
            // Enlarged Profile Avatar
            ProfileAvatar(
              username: widget.username,
              imageUrl: widget.profileImageUrl,
              size: 120.0, // Larger size for profile screen
            ),
            const SizedBox(height: 20),
            // Username
            Text(
              widget.username,
              style: const TextStyle(
                fontSize: 24.0,
                fontWeight: FontWeight.bold,
              ),
            ),
            const SizedBox(height: 20),
            // Custom text fields for user details
            TextFormField(
              controller: birthdayController,
              decoration: const InputDecoration(
                labelText: 'Birthday',
                border: OutlineInputBorder(),
              ),
              readOnly: true,
              onTap: _selectBirthday, // Trigger date picker on tap
            ),
            const SizedBox(height: 10),
            CustomTextField(
              labelText: 'Height',
              controller: heightController,
            ),
            const SizedBox(height: 10),
            CustomTextField(
              labelText: 'Weight',
              controller: weightController,
            ),
            const SizedBox(height: 10),
            CustomTextField(
              labelText: 'Gender',
              controller: genderController,
            ),
            const SizedBox(height: 10),
            CustomTextField(
              labelText: 'Constraints/Injury',
              controller: constraintsController,
            ),
            const SizedBox(height: 10),
            CustomTextField(
              labelText: 'Workout Purpose',
              controller: workoutPurposeController,
            ),
            const SizedBox(height: 20),
            // Use the SubmitButton widget
            SubmitButton(
              text: 'Save',
              onPressed: () {
                // Logic to save or submit user details
                print('Birthday: ${birthdayController.text}');
                print('Height: ${heightController.text}');
                print('Weight: ${weightController.text}');
                print('Gender: ${genderController.text}');
                print('Constraints: ${constraintsController.text}');
                print('Workout Purpose: ${workoutPurposeController.text}');
              },
              backgroundColor: Colors.green,
              textColor: Colors.white,
              borderRadius: 12.0,
            ),
          ],
        ),
      ),
    );
  }
}
