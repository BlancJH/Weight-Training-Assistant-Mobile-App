import 'package:flutter/material.dart';

enum MenuOptions { profile, settings, logout }

class PopupMenuWidget extends StatelessWidget {
  final Widget trigger;
  final ValueChanged<MenuOptions> onSelected;

  const PopupMenuWidget({
    required this.trigger,
    required this.onSelected,
    Key? key,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return PopupMenuButton<MenuOptions>(
      onSelected: onSelected,
      offset: const Offset(0, 50), // Adjusts the position
      itemBuilder: (BuildContext context) => [
        PopupMenuItem(
          value: MenuOptions.profile,
          child: Row(
            children: const [
              Icon(Icons.person, color: Colors.blue),
              SizedBox(width: 10),
              Text('View Profile'),
            ],
          ),
        ),
        PopupMenuItem(
          value: MenuOptions.settings,
          child: Row(
            children: const [
              Icon(Icons.settings, color: Colors.grey),
              SizedBox(width: 10),
              Text('Settings'),
            ],
          ),
        ),
        PopupMenuItem(
          value: MenuOptions.logout,
          child: Row(
            children: const [
              Icon(Icons.logout, color: Colors.red),
              SizedBox(width: 10),
              Text('Logout'),
            ],
          ),
        ),
      ],
      child: trigger,
    );
  }
}
