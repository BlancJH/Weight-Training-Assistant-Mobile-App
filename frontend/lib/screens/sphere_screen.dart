import 'package:flutter/material.dart';
import '../widgets/sphere_widget.dart';

class SpherePage extends StatelessWidget {
  final String? username;
  final String? profileUrl;

  const SpherePage({this.username, this.profileUrl, Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    return Scaffold(
      // No AppBar here because HomeScreen's AppBar is used.
      body: Center(
        child: SizedBox(
          height: 400,
          child: SphereWidget(
            imageUrl: 'assets/images/Rocky.jpeg',
            size: 250,
          ),
        ),
      ),
    );
  }
}