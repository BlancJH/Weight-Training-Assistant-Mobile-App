import 'package:flutter/material.dart';
import '../services/auth_service.dart';
import '../utils/design_utils.dart';
import '../utils/validator.dart';
import '../widgets/alert_widget.dart';
import '../widgets/profile_avatar.dart';
import '../widgets/custom_text_field.dart';
import '../widgets/submit_button.dart';
import '../utils/conversion_utils.dart';
import 'package:image_picker/image_picker.dart'; 
import 'dart:io';
import '../services/user_service.dart';
import '../services/profile_service.dart';
import 'package:intl/intl.dart';

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
  final GlobalKey<FormState> _formKey = GlobalKey<FormState>();
  final TextEditingController birthdayController = TextEditingController();
  final TextEditingController heightController = TextEditingController();
  final TextEditingController weightController = TextEditingController();
  final TextEditingController genderController = TextEditingController();

  String _activeHeightUnit = 'cm'; // Default unit for height
  String _activeWeightUnit = 'kg'; // Default unit for weight
  

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
      print("Fetched user details: $userDetails");

      // Populate controllers with the fetched data
      setState(() {

        // Convert `birthday` to a formatted string if it exists
        final dob = userDetails['dob'];
        birthdayController.text = (dob != null && dob.isNotEmpty)
            ? DateFormat('yyyy-MM-dd').format(DateTime.parse(dob))
            : ''; // Convert to string or set empty if null

        // Convert height value double to string
        heightController.text = (userDetails['heightValue'] != null)
            ? userDetails['heightValue'].toString()
            : '';

        // Height unit
        _activeHeightUnit = userDetails['heightUnit'] ?? 'cm';

        // Convert weight double to string
        weightController.text = (userDetails['weightValue'] != null)
          ? userDetails['weightValue'].toString()
          : '';

        // Weight unit
        _activeWeightUnit = userDetails['weightUnit'] ?? 'kg';

        // Gender
        genderController.text = userDetails['gender'] ?? '';

      });
    } catch (e) {
      final errorMessage = e.toString().replaceFirst("Exception: ", "");
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('$errorMessage')),
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

  // DOB selector config
  Future<void> _selectBirthday() async {
    DateTime? pickedDate = await showDatePicker(
      context: context,
      initialDate: DateTime(2000),
      firstDate: DateTime(1900),
      lastDate: DateTime.now(),
    );


    // DOB format
    if (pickedDate != null) {
      setState(() {
        // Format the date to yyyy-MM-dd
        birthdayController.text = DateFormat('yyyy-MM-dd').format(pickedDate);
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
    final theme = Theme.of(context);

    return Scaffold(
      appBar: AppBar(
        title: const Text('Profile'),
        backgroundColor: theme.colorScheme.surface,
        elevation: 0,
        iconTheme: const IconThemeData(),
        centerTitle: true,
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16.0),
        child: Form(
          key: _formKey,
          autovalidateMode: AutovalidateMode.onUserInteraction, // Enable live validation
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
                                validator: (value) => Validators.validatePositiveDouble(value, 'Height'),
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
                                          ? buttonColor
                                          : primaryTextColor,
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
                                          ? buttonColor
                                          : primaryTextColor,
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
                                validator: (value) => Validators.validatePositiveDouble(value, 'Weight'),
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
                                          ? buttonColor
                                          : primaryTextColor
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
                                          ? buttonColor
                                          : primaryTextColor
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
                    formKey: _formKey,
                    birthday: birthdayController.text,
                    heightValue: heightValue,
                    heightUnit: _activeHeightUnit,
                    weightValue: weightValue,
                    weightUnit: _activeWeightUnit,
                    gender: genderController.text,
                  );
                },
              ),
              const SizedBox(height: 20),
              Row(
                children: [
                  Expanded(
                    child: Divider(
                      thickness: 1,
                      color: Colors.grey,
                    ),
                  ),
                  TextButton(
                    onPressed: () async {
                      AlertWidget.show(
                        context: context,
                        title: "Delete Account",
                        content:
                            "Are you sure you want to delete your account? This action cannot be undone.",
                        cancelText: "Cancel",
                        confirmText: "Delete",
                        onConfirm: () async {
                          try {
                            // Call the service to disable the account.
                            bool success = await AuthService().disableUserAccount();
                            if (success) {
                              ScaffoldMessenger.of(context).showSnackBar(
                                SnackBar(content: Text("Account disabled successfully.")),
                              );
                              // Log out the user.
                              await AuthService().logoutUser();
                              // Navigate to the login screen or initial screen.
                              Navigator.of(context).pushNamedAndRemoveUntil('/login', (route) => false);
                            }
                          } catch (e) {
                            ScaffoldMessenger.of(context).showSnackBar(
                              SnackBar(content: Text("Error disabling account: $e")),
                            );
                          }
                        },
                      );
                    },
                    child: const Text(
                      'Delete Account',
                      style: TextStyle(color: Colors.red),
                    ),
                  ),
                  Expanded(
                    child: Divider(
                      thickness: 1,
                      color: Colors.grey,
                    ),
                  ),
                ],
              ),
            ],
          ),
        ),
      ),
    );
  }
}