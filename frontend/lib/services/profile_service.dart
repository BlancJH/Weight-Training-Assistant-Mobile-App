import 'package:flutter/material.dart';
import '../services/user_service.dart';

class ProfileService {
  final UserService userService;

  ProfileService({required this.userService});

  Future<void> saveProfile({
    required BuildContext context,
    String? birthday,
    String? heightValue,
    String? heightUnit,
    String? weightValue,
    String? weightUnit,
    String? gender,
    String? constraints,
    String? workoutPurpose,
  }) async {
    // Construct profile data only with non-null values
    final profileData = <String, dynamic>{};

    if (birthday != null && birthday.isNotEmpty) {
      profileData['birthday'] = birthday;
    }
    if (heightValue != null && heightValue.isNotEmpty) {
      profileData['height_value'] = heightValue;
    }
    if (heightUnit != null && heightUnit.isNotEmpty) {
      profileData['height_unit'] = heightUnit;
    }
    if (weightValue != null && weightValue.isNotEmpty) {
      profileData['weight_value'] = weightValue;
    }
    if (weightUnit != null && weightUnit.isNotEmpty) {
      profileData['weight_unit'] = weightUnit;
    }
    if (gender != null && gender.isNotEmpty) {
      profileData['gender'] = gender;
    }
    if (constraints != null && constraints.isNotEmpty) {
      profileData['constraints'] = constraints;
    }
    if (workoutPurpose != null && workoutPurpose.isNotEmpty) {
      profileData['workoutPurpose'] = workoutPurpose;
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
