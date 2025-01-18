import 'package:flutter/material.dart';
import 'package:frontend_1/utils/validator.dart';
import '../widgets/profile_avatar.dart';
import '../widgets/custom_text_field.dart';
import '../widgets/submit_button.dart';
import '../utils/conversion_utils.dart';
import 'package:image_picker/image_picker.dart'; 
import 'dart:io';
import '../services/user_service.dart';
import '../services/profile_service.dart';

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

  String _activeHeightUnit = 'cm'; // Default unit for height
  String _activeWeightUnit = 'kg'; // Default unit for weight
  String? constraintsError; // Holds the error message for Constraints field
  String? workoutPurposeError; // Holds the error message for Workout Purpose field
  
  int constraintMaxLength = 20; // Charactor limit for constraints/injuries field.
  int workoutPurposeMaxLength = 20; // Charactor limit for workout purpose field.

  File? _profileImage; // Holds the selected profile image file

  final ProfileService profileService = ProfileService(userService: UserService());
  bool _isLoading = true; // To track if data is being fetched

  @override
  void initState() {
    super.initState();
    _loadUserProfile(); // Fetch user profile data on screen load
  }

  // Function to load profile data
  Future<void> _loadUserProfile() async {
    try {
      final userDetails = await profileService.loadProfile(context);

      // Populate controllers with the fetched data
      setState(() {
        birthdayController.text = userDetails['birthday'] ?? '';
        heightController.text = (userDetails['heightValue'] != null)
            ? userDetails['heightValue'].toString()
            : '';
        _activeHeightUnit = userDetails['heightUnit'] ?? 'cm';
        weightController.text = (userDetails['weightValue'] != null)
          ? userDetails['weightValue'].toString()
          : '';
        _activeWeightUnit = userDetails['weightUnit'] ?? 'kg';
        genderController.text = userDetails['gender'] ?? '';
        constraintsController.text = userDetails['injuriesOrConstraints'] ?? '';
        workoutPurposeController.text = userDetails['purpose'] ?? '';
      });
    } catch (e) {
      // Show error message if loading fails
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('Error loading profile: $e')),
      );
    } finally {
      setState(() {
        _isLoading = false; // Data loading completed
      });
    }
  }

  // Function to handle image picking
  Future<void> _pickImage() async {
    final ImagePicker picker = ImagePicker();
    final XFile? pickedFile = await picker.pickImage(
      source: ImageSource.gallery, // Opens the device gallery
      imageQuality: 80, // Reduce quality to optimize file size
    );

    if (pickedFile != null) {
      setState(() {
        _profileImage = File(pickedFile.path); // Update the state with the selected image
      });

      // You can upload the file to a server or storage service here
      print('Selected Image Path: ${pickedFile.path}');
    }
  }

  // Function to validate constraints live
  void _validateConstraints(String value) {
    setState(() {
      constraintsError = Validators.validateCharacterLimit(value, 'Constraints/Injuries', constraintMaxLength);
    });
  }

  // Function to validate constraints live
  void _validateWorkoutPurpose(String value) {
    setState(() {
      constraintsError = Validators.validateCharacterLimit(value, 'Workout Purpose', workoutPurposeMaxLength);
    });
  }

  // DOB selector config
  Future<void> _selectBirthday() async {
    DateTime? pickedDate = await showDatePicker(
      context: context,
      initialDate: DateTime(2000),
      firstDate: DateTime(1900),
      lastDate: DateTime.now(),
    );


    // DOB display format
    if (pickedDate != null) {
      setState(() {
        birthdayController.text = "${pickedDate.year}-${pickedDate.month}-${pickedDate.day}";
      });
    }
  }

  // Height unit toggle
  void _handleHeightUnitChange(String unit) {
    if (_activeHeightUnit != unit) {
      setState(() {
        double currentHeight = double.tryParse(heightController.text) ?? 0.0;

        if (unit == 'ft') {
          heightController.text = ConversionUtils.cmToFeet(currentHeight).toStringAsFixed(2);
        } else {
          heightController.text = ConversionUtils.feetToCm(currentHeight).toStringAsFixed(2);
        }

        _activeHeightUnit = unit;
      });
    }
  }

  // Weight unit toggle
  void _handleWeightUnitChange(String unit) {
    if (_activeWeightUnit != unit) {
      setState(() {
        double currentWeight = double.tryParse(weightController.text) ?? 0.0;

        if (unit == 'lbs') {
          weightController.text = ConversionUtils.kgToLbs(currentWeight).toStringAsFixed(2);
        } else {
          weightController.text = ConversionUtils.lbsToKg(currentWeight).toStringAsFixed(2);
        }

        _activeWeightUnit = unit;
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

            // Profile widget
            Stack(
              alignment: Alignment.bottomRight,
              children: [
                ProfileAvatar(
                  username: widget.username,
                  imageUrl: widget.profileImageUrl,
                  size: 120.0,
                ),
                Positioned(
                  right: 0,
                  bottom: 0,
                  child: GestureDetector(
                    onTap: _pickImage,
                    child: CircleAvatar(
                      radius: 20, // Adjust size of the edit button
                      backgroundColor: Colors.grey,
                      child: const Icon(
                        Icons.edit,
                        size: 16.0,
                        color: Colors.white,
                      ),
                    ),
                  ),
                ),
              ],
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

            // DOB
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

            // Height and Weight in a Row
            Row(
              children: [
                // Height Column
                Expanded(
                  child: Column(
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
                                onTap: () => _handleHeightUnitChange('ft'),
                                child: Text(
                                  'ft',
                                  style: TextStyle(
                                    fontSize: 16.0,
                                    fontWeight: _activeHeightUnit == 'ft'
                                        ? FontWeight.bold
                                        : FontWeight.normal,
                                    color: _activeHeightUnit == 'ft'
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
                ),
                const SizedBox(width: 20), // Space between columns

                // Weight Column
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      const SizedBox(height: 5),
                      Row(
                        children: [
                          Expanded(
                            child: CustomTextField(
                              labelText: 'Weight',
                              controller: weightController,
                              validator: (value) {
                                if (value == null || value.isEmpty) {
                                  return 'Please enter your weight';
                                }
                                return null;
                              },
                            ),
                          ),
                          const SizedBox(width: 10),
                          Row(
                            children: [
                              GestureDetector(
                                onTap: () => _handleWeightUnitChange('kg'),
                                child: Text(
                                  'kg',
                                  style: TextStyle(
                                    fontSize: 16.0,
                                    fontWeight: _activeWeightUnit == 'kg'
                                        ? FontWeight.bold
                                        : FontWeight.normal,
                                    color: _activeWeightUnit == 'kg'
                                        ? Colors.blue
                                        : Colors.black,
                                  ),
                                ),
                              ),
                              const Text(' | '),
                              GestureDetector(
                                onTap: () => _handleWeightUnitChange('lbs'),
                                child: Text(
                                  'lbs',
                                  style: TextStyle(
                                    fontSize: 16.0,
                                    fontWeight: _activeWeightUnit == 'lbs'
                                        ? FontWeight.bold
                                        : FontWeight.normal,
                                    color: _activeWeightUnit == 'lbs'
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
                ),
              ],
            ),
            const SizedBox(height: 10),

            // Gender Dropdown
            Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                const SizedBox(height: 5),
                DropdownButtonFormField<String>(
                  value: genderController.text.isNotEmpty ? genderController.text : null,
                  decoration: const InputDecoration(
                    border: OutlineInputBorder(),
                  ),
                  hint: const Text('Select Gender'),
                  items: [
                    DropdownMenuItem(
                      value: 'Male',
                      child: Text('Male (XY)'),
                    ),
                    DropdownMenuItem(
                      value: 'Female',
                      child: Text('Female (XX)'),
                    ),
                  ],
                  onChanged: (value) {
                    setState(() {
                      genderController.text = value ?? '';
                    });
                  },
                ),
              ],
            ),
            const SizedBox(height: 20),

            // Constraints with Live Error
            CustomTextField(
              labelText: 'Constraints/Injuries',
              controller: constraintsController,
              validator: (value) => constraintsError,
              maxLength: constraintMaxLength,
              onChanged: _validateConstraints, // Live validation
            ),
            if (constraintsError != null) // Check if there's an error
              Align(
                alignment: Alignment.centerLeft, // Align error text to the left
                child:
                Padding(
                  padding: const EdgeInsets.only(top: 4.0),
                  child: Text(
                    constraintsError!,
                    style: TextStyle(color: Colors.red, fontSize: 12.0),
                  ),
                ),
              ),
            const SizedBox(height: 10),

            // Workout purpose
            CustomTextField(
              labelText: 'Workout Purpose',
              controller: workoutPurposeController,
              validator: (value) => workoutPurposeError,
              maxLength: workoutPurposeMaxLength,
              onChanged: _validateWorkoutPurpose, // Live validation
            ),
            if (workoutPurposeError != null) // Check if there's an error
              Align(
                alignment: Alignment.centerLeft, // Align error text to the left
                child:
                Padding(
                  padding: const EdgeInsets.only(top: 4.0),
                  child: Text(
                    workoutPurposeError!,
                    style: TextStyle(color: Colors.red, fontSize: 12.0),
                  ),
                ),
              ),
            const SizedBox(height: 20),

            // Submit button
            SubmitButton(
              text: 'Save',
              onPressed: () async {
                // Parse height and weight from text controllers (nullable)
                final double? heightValue = heightController.text.isNotEmpty
                    ? double.tryParse(heightController.text)
                    : null;
                final double? weightValue = weightController.text.isNotEmpty
                    ? double.tryParse(weightController.text)
                    : null;

                await profileService.saveProfile(
                  context: context,
                  birthday: birthdayController.text,
                  heightValue: heightValue,
                  heightUnit: _activeHeightUnit,
                  weightValue: weightValue,
                  weightUnit: _activeWeightUnit,
                  gender: genderController.text,
                  constraints: constraintsController.text,
                  workoutPurpose: workoutPurposeController.text,
                );

                // Diagnostic prints
                print("Birthday: ${birthdayController.text}");
                print("Height Value: $heightValue");
                print("Height Unit: $_activeHeightUnit");
                print("Weight Value: $weightValue");
                print("Weight Unit: $_activeWeightUnit");
                print("Gender: ${genderController.text}");
                print("Constraints: ${constraintsController.text}");
                print("Workout Purpose: ${workoutPurposeController.text}");
              },
            ),
          ],
        ),
      ),
    );
  }
}