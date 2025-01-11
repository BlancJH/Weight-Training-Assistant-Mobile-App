import 'package:flutter/material.dart';
import '../widgets/profile_avatar.dart';
import '../widgets/custom_text_field.dart';
import '../widgets/submit_button.dart';
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
  final TextEditingController birthdayController = TextEditingController();
  final TextEditingController heightController = TextEditingController();
  final TextEditingController weightController = TextEditingController();
  final TextEditingController genderController = TextEditingController();
  final TextEditingController constraintsController = TextEditingController();
  final TextEditingController workoutPurposeController = TextEditingController();

  String _activeHeightUnit = 'cm'; // Default unit is cm

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
            ProfileAvatar(
              username: widget.username,
              imageUrl: widget.profileImageUrl,
              size: 120.0,
            ),
            const SizedBox(height: 20),
            Text(
              widget.username,
              style: const TextStyle(
                fontSize: 24.0,
                fontWeight: FontWeight.bold,
              ),
            ),
            const SizedBox(height: 20),
            TextFormField(
              controller: birthdayController,
              decoration: const InputDecoration(
                labelText: 'Birthday',
                border: OutlineInputBorder(),
              ),
              readOnly: true,
              onTap: _selectBirthday,
            ),
            const SizedBox(height: 10),
            Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                const SizedBox(height: 5),
                Row(
                  children: [
                    Expanded(
                      child: CustomTextField(
                        labelText: 'Height',
                        controller: heightController,
                        validator: (value) {
                          if (value == null || value.isEmpty) {
                            return 'Please enter your height';
                          }
                          return null;
                        },
                      ),
                    ),
                    const SizedBox(width: 10),
                    Row(
                      children: [
                        GestureDetector(
                          onTap: () => _handleHeightUnitChange('cm'),
                          child: Text(
                            'cm',
                            style: TextStyle(
                              fontSize: 16.0,
                              fontWeight: _activeHeightUnit == 'cm'
                                  ? FontWeight.bold
                                  : FontWeight.normal,
                              color: _activeHeightUnit == 'cm'
                                  ? Colors.blue
                                  : Colors.black,
                            ),
                          ),
                        ),
                        const Text(' | '),
                        GestureDetector(
                          onTap: () => _handleHeightUnitChange('feet'),
                          child: Text(
                            'feet',
                            style: TextStyle(
                              fontSize: 16.0,
                              fontWeight: _activeHeightUnit == 'feet'
                                  ? FontWeight.bold
                                  : FontWeight.normal,
                              color: _activeHeightUnit == 'feet'
                                  ? Colors.blue
                                  : Colors.black,
                            ),
                          ),
                        ),
                      ],
                    ),
                  ],
                ),
              ],
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
            SubmitButton(
              text: 'Save',
              onPressed: () {
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
