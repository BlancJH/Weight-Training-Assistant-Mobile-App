import 'package:flutter/material.dart';
import '../widgets/sphere_widget.dart';
import '../utils/design_utils.dart';

class SpherePage extends StatelessWidget {
  final String? username;
  final String? profileUrl;
  final String? sphereName;
  final int? sphereLevel;

  const SpherePage({
    this.username,
    this.profileUrl,
    this.sphereName,
    this.sphereLevel,
    Key? key,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    return Scaffold(
      // No AppBar here because HomeScreen's AppBar is used.
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            SizedBox(
              height: 400,
              child: SphereWidget(
                imageUrl: 'assets/images/Rocky.jpeg',
                level: sphereLevel ?? 1,
                baseSize: 300,
              ),
            ),
            const SizedBox(height: 20),
            Text(
              sphereName?? 'Rocky',
              style: theme.textTheme.displayLarge,
            ),
            Text(
              'Level: ${sphereLevel ?? 1}',
              style: theme.textTheme.titleLarge?.copyWith(fontSize: 20),
            ),
          ],
        ),
      ),
    );
  }
}
