import 'package:flutter/material.dart';

class ProfileAvatar extends StatelessWidget {
  final String? imageUrl; // URL for the profile picture
  final String username; // Fallback username for initials
  final double size; // Size of the avatar

  const ProfileAvatar({
    this.imageUrl, // Optional image URL
    required this.username, // Required username
    this.size = 40.0, // Default size
    Key? key,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return CircleAvatar(
      radius: size / 2,
      backgroundImage: (imageUrl != "Default" && imageUrl?.isNotEmpty == true)
          ? NetworkImage(imageUrl!) // Show image if URL is valid
          : null,
      backgroundColor: Colors.grey.shade200, // Default background color
      child: (imageUrl == "Default" || imageUrl?.isEmpty == true)
          ? Text(
              _getInitials(username),
              style: TextStyle(
                fontSize: size / 2.5,
                color: Colors.white,
              ),
            )
          : null,
    );
  }

  // Extract initials from the username
  String _getInitials(String username) {
    final words = username.split(' ');
    final initials = words.take(2).map((word) => word.isNotEmpty ? word[0] : '').join();
    return initials.toUpperCase();
  }
}
