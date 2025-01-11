import 'package:flutter/material.dart';
import '../widgets/profile_avatar.dart';
import '../widgets/custom_text_field.dart';
import '../widgets/submit_button.dart';
import '../widgets/unit_toggle.dart';
import '../utils/conversion_utils.dart';

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

  String _activeHeightUnit = 'cm'; // Default unit for height

  // Handle height unit changes
  void _handleHeightUnitChange(String unit) {
    if (_activeHeightUnit != unit) {
      setState(() {
        double currentHeight = double.tryParse(heightController.text) ?? 0.0;

        if (unit == 'feet') {
          heightController.text = ConversionUtils.cmToFeet(currentHeight).toStringAsFixed(2);
        } else {
          heightController.text = ConversionUtils.feetToCm(currentHeight).toStringAsFixed(2);
        }

        _activeHeightUnit = unit;
      });
    }
  }

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
        birthdayController.text = "${pickedDate.year}/${pickedDate.month}/${pickedDate.day}";
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
            // Birthday
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
            // Height with UnitToggle
            Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                const Text(
                  'Height',
                  style: TextStyle(fontSize: 16.0, fontWeight: FontWeight.w500),
                ),
                const SizedBox(height: 5),
                Row(
                  children: [
                    Expanded(
                      child: TextFormField(
                        controller: heightController,
                        keyboardType: TextInputType.number,
                        decoration: const InputDecoration(
                          border: OutlineInputBorder(),
                          hintText: 'Enter height',
                        ),
                      ),
                    ),
                    const SizedBox(width: 10),
                    UnitToggle(
                      activeUnit: _activeHeightUnit,
                      units: const ['cm', 'feet'],
                      onUnitChanged: _handleHeightUnitChange,
                    ),
                  ],
                ),
              ],
            ),
            const SizedBox(height: 10),
            // Weight without toggle
            CustomTextField(
              labelText: 'Weight',
              controller: weightController,
            ),
            const SizedBox(height: 10),
            // Other fields
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
            // Save Button
            SubmitButton(
              text: 'Save',
              onPressed: () {
                // Logic to save or submit user details
                print('Birthday: ${birthdayController.text}');
                print('Height: ${heightController.text} $_activeHeightUnit');
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
