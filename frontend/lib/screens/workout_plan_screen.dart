import 'package:flutter/material.dart';
import 'package:frontend_1/utils/validator.dart';
import '../widgets/custom_text_field.dart';
import '../widgets/submit_button.dart';
import '../utils/conversion_utils.dart';
import '../services/user_service.dart';
import '../services/profile_service.dart';
import 'package:intl/intl.dart';
import '../widgets/custom_time_picker_dialog.dart';
import '../services/exercise_plan_service.dart';
import '../services/auth_service.dart';

class WorkoutPlanScreen extends StatefulWidget {
  final String username;
  
  const WorkoutPlanScreen({
    required this.username,
    Key? key,
  }) : super(key: key);

  @override
  State<WorkoutPlanScreen> createState() => _WorkoutPlanScreenState();
}

class _WorkoutPlanScreenState extends State<WorkoutPlanScreen> {
  final GlobalKey<FormState> _formKey = GlobalKey<FormState>();
  final TextEditingController birthdayController = TextEditingController();
  final TextEditingController heightController = TextEditingController();
  final TextEditingController weightController = TextEditingController();
  final TextEditingController genderController = TextEditingController();
  final TextEditingController constraintsController = TextEditingController();
  final TextEditingController workoutPurposeController = TextEditingController();
  final TextEditingController workoutFrequencyController = TextEditingController();
  final TextEditingController workoutDurationController = TextEditingController();
  final TextEditingController workoutSplitController = TextEditingController();

  String _activeHeightUnit = 'cm'; // Default unit for height
  String _activeWeightUnit = 'kg'; // Default unit for weight
  String? constraintsError; // Holds the error message for Constraints field
  String? workoutPurposeError; // Holds the error message for Workout Purpose field
  
  int constraintMaxLength = 20; // Charactor limit for constraints/injuries field.
  int workoutPurposeMaxLength = 20; // Charactor limit for workout purpose field.

  final ProfileService profileService = ProfileService(userService: UserService());
  final ExercisePlanService exercisePlanService = ExercisePlanService();
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

        // Constraints
        constraintsController.text = userDetails['injuriesOrConstraints'] ?? '';

        // Workout purpose
        workoutPurposeController.text = userDetails['purpose'] ?? '';

        // Workout Frequency
        workoutFrequencyController.text = userDetails['workoutFrequency'] ?? '';

        // Workout Duration
        workoutDurationController.text = (userDetails['workoutDuration'] != null)
         ? userDetails['workoutDuration'].toString()
         :'';

        // Workout Split
        workoutSplitController.text = (userDetails['numberOfSplit'] != null)
         ? userDetails['numberOfSplit'].toString()
         :'';
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

  // Function to validate constraints live
  void _validateConstraints(String value) {
    setState(() {
      constraintsError = Validators.validateCharacterLimit(value, 'Constraints/Injuries', constraintMaxLength);
    });
  }

  // Function to validate constraints live
  void _validateWorkoutPurpose(String value) {
    setState(() {
      workoutPurposeError = Validators.validateCharacterLimit(value, 'Workout Purpose', workoutPurposeMaxLength);
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
    return Scaffold(
      appBar: AppBar(
        title: const Text('Workout Plan'),
        backgroundColor: Colors.white,
        elevation: 0,
        iconTheme: const IconThemeData(color: Colors.black),
        centerTitle: true,
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16.0),
        child: Form(
          key: _formKey,
          autovalidateMode: AutovalidateMode.onUserInteraction, // Enable live validation
          child: Column(
            children: [

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
              const SizedBox(height: 10),

              // Workout purpose
              CustomTextField(
                labelText: 'Workout Purpose',
                controller: workoutPurposeController,
                validator: (value) => workoutPurposeError,
                maxLength: workoutPurposeMaxLength,
                onChanged: _validateWorkoutPurpose, // Live validation
              ),
              const SizedBox(height: 10),

              // Workout Frequency (String)
              Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  const SizedBox(height: 5),
                  DropdownButtonFormField<String>(
                    value: workoutFrequencyController.text.isNotEmpty ? workoutFrequencyController.text : null,
                    decoration: const InputDecoration(
                      border: OutlineInputBorder(),
                    ),
                    hint: const Text('Select Frequency'),
                    items: List.generate(
                      7,
                      (index) => DropdownMenuItem(
                        value: '${index + 1}', // The value as a string
                        child: Text('${index + 1} ${index + 1 == 1 ? "Time" : "Times"} a Week'),
                      ),
                    ),
                    onChanged: (value) {
                      setState(() {
                        workoutFrequencyController.text = value ?? '';
                      });
                    },
                  ),
                ],
              ),
              const SizedBox(height: 10),

              // Workout Duration (Integer)
              Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  const SizedBox(height: 5),
                  TextFormField(
                    controller: workoutDurationController,
                    decoration: const InputDecoration(
                      labelText: 'Duration (minutes)',
                      border: OutlineInputBorder(),
                    ),
                    readOnly: true,
                    onTap: () async {
                      final selectedMinutes = await showDialog<int>(
                        context: context,
                        builder: (context) => CustomTimePickerDialog(
                          initialMinutes: int.tryParse(workoutDurationController.text) ?? 0,
                        ),
                      );

                      if (selectedMinutes != null) {
                        setState(() {
                          workoutDurationController.text = selectedMinutes.toString();
                        });
                      }
                    },
                  ),
                ],
              ),
              const SizedBox(height: 10),

              // Number of Split
              Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  const SizedBox(height: 5),
                  DropdownButtonFormField<String>(
                    value: workoutSplitController.text.isNotEmpty ? workoutSplitController.text : null,
                    decoration: const InputDecoration(
                      border: OutlineInputBorder(),
                    ),
                    hint: const Text('Select Splits'),
                    items: List.generate(
                      7,
                      (index) => DropdownMenuItem(
                        value: '${index + 1}',
                        child: Text('${index + 1}'),
                      ),
                    ),
                    onChanged: (value) {
                      setState(() {
                        workoutSplitController.text = value ?? '';
                      });
                    },
                  ),
                ],
              ),

            ],
          ),
        ),
      ),
      bottomNavigationBar: Container(
        padding: const EdgeInsets.symmetric(horizontal: 16.0, vertical: 40.0),
        child: 
        // Submit Button
        SubmitButton(
          text: 'Workout Plan Request!',
          onPressed: () async {
            if (!_formKey.currentState!.validate()) {
              // Show error if form is invalid
              ScaffoldMessenger.of(context).showSnackBar(
                const SnackBar(content: Text('Please fix errors before submitting!')),
              );
              return;
            }

            try {
              // Parse form data
              final double? heightValue = heightController.text.isNotEmpty
                  ? double.tryParse(heightController.text)
                  : null;
              final double? weightValue = weightController.text.isNotEmpty
                  ? double.tryParse(weightController.text)
                  : null;
              final int? workoutDuration = workoutDurationController.text.isNotEmpty
                  ? int.tryParse(workoutDurationController.text)
                  : null;
              final int? numberOfSplit = workoutSplitController.text.isNotEmpty
                  ? int.tryParse(workoutSplitController.text)
                  : null;

              // Prepare the user details
              final userDetails = {
                'username': widget.username,
                'birthday': birthdayController.text,
                'heightValue': heightValue,
                'heightUnit': _activeHeightUnit,
                'weightValue': weightValue,
                'weightUnit': _activeWeightUnit,
                'gender': genderController.text,
                'constraints': constraintsController.text,
                'purpose': workoutPurposeController.text,
                'workoutFrequency': workoutFrequencyController.text,
                'workoutDuration': workoutDuration,
                'numberOfSplit': numberOfSplit,
              };

              // Save profile data
              await profileService.saveProfile(
                context: context,
                formKey: _formKey,
                birthday: birthdayController.text,
                heightValue: heightValue,
                heightUnit: _activeHeightUnit,
                weightValue: weightValue,
                weightUnit: _activeWeightUnit,
                gender: genderController.text,
                constraints: constraintsController.text,
                workoutPurpose: workoutPurposeController.text,
                workoutFrequency: workoutFrequencyController.text,
                workoutDuration: workoutDuration,
                numberOfSplit: numberOfSplit,
              );

              // Fetch JWT token
              final jwtToken = await AuthService().getToken();

              if (jwtToken == null) {
                ScaffoldMessenger.of(context).showSnackBar(
                  const SnackBar(content: Text('Error: No JWT token found. Please log in.')),
                );
                return;
              }

              // Send workout plan request
              final responseMessage = await exercisePlanService.sendUserDetails(userDetails, jwtToken);

              // Show success message from workout plan service
              ScaffoldMessenger.of(context).showSnackBar(
                SnackBar(content: Text(responseMessage)),
              );
            } catch (e) {
              // Handle errors
              ScaffoldMessenger.of(context).showSnackBar(
                SnackBar(content: Text('Error: ${e.toString()}')),
              );
            }
          },
        ),
      ),
    );
  }
}