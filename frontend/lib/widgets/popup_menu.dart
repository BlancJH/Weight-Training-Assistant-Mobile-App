import 'package:flutter/material.dart';

enum MenuOptions { profile, settings, logout }

class PopupMenuWidget extends StatelessWidget {
  final ValueChanged<MenuOptions> onSelected;
  final Widget trigger;

  const PopupMenuWidget({
    Key? key,
    required this.onSelected,
    required this.trigger,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return PopupMenuButton<MenuOptions>(
      onSelected: onSelected,
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
