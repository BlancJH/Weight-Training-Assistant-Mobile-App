import 'package:cloud_firestore/cloud_firestore.dart';

class UserService {
  final FirebaseFirestore _firestore = FirebaseFirestore.instance;

  Future<void> updateUserProfile(String userId, Map<String, dynamic> profileData) async {
    try {
      await _firestore.collection('users').doc(userId).update(profileData);
    } catch (e) {
      print('Failed to update user profile: $e');
    }
  }
}
