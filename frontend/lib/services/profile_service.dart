import 'package:flutter/material.dart';
import '../services/user_service.dart';

class ProfileService {
  final UserService userService;

  ProfileService({required this.userService});

  Future<Map<String, dynamic>> loadProfile(BuildContext context) async {
    try {
      final userDetails = await userService.fetchUserDetails();
      return userDetails;
    } catch (e) {
      _showSnackBar(context, 'Error loading profile: $e');
      throw e;
    }
  }

  Future<void> saveProfile({
    required BuildContext context,
    required GlobalKey<FormState> formKey,
    String? birthday,
    double? heightValue,
    String? heightUnit,
    double? weightValue,
    String? weightUnit,
    String? gender,
    String? constraints,
    String? workoutPurpose,
  }) async {
      // Validate form fields
    if (!formKey.currentState!.validate()) {
      _showSnackBar(context, 'Please fix errors before submitting!');
      return;
    }
    // Construct profile data only with non-null values
    final profileData = <String, dynamic>{};

    if (birthday != null && birthday.isNotEmpty) {
      profileData['dob'] = birthday;
    }
    if (heightValue != null) {
      profileData['heightValue'] = heightValue;
    }
    if (heightUnit != null && heightUnit.isNotEmpty) {
      profileData['heightUnit'] = heightUnit;
    }
    if (weightValue != null) {
      profileData['weightValue'] = weightValue;
    }
    if (weightUnit != null && weightUnit.isNotEmpty) {
      profileData['weightUnit'] = weightUnit;
    }
    if (gender != null && gender.isNotEmpty) {
      profileData['gender'] = gender;
    }
    if (constraints != null && constraints.isNotEmpty) {
      profileData['injuriesOrConstraints'] = constraints;
    }
    if (workoutPurpose != null && workoutPurpose.isNotEmpty) {
      profileData['purpose'] = workoutPurpose;
    }

    try {
      // Call the backend service to save/update profile
      await userService.saveOrUpdateUserDetails(profileData);

      // Show success message
      _showSnackBar(context, 'Profile updated successfully!');
    } catch (e) {
      // Show error message in case of failure
      _showSnackBar(context, 'Error updating profile: $e');
    }
  }

  void _showSnackBar(BuildContext context, String message) {
    ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text(message)));
  }
}
