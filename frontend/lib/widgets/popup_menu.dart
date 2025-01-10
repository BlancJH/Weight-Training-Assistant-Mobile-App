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
      offset: const Offset(0, 50), // Adjusts the position (x, y)
      itemBuilder: (BuildContext context) => [
        PopupMenuItem(
          value: MenuOptions.profile,
          child: Text('View Profile'),
        ),
        PopupMenuItem(
          value: MenuOptions.settings,
          child: Text('Settings'),
        ),
        PopupMenuItem(
          value: MenuOptions.logout,
          child: Text('Logout'),
        ),
      ],
      child: trigger,
    );
  }
}
