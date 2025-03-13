import 'package:flutter/material.dart';
import 'package:frontend_1/screens/sphere_inventory_screen.dart';
import 'package:frontend_1/utils/design_utils.dart';
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
      body: Stack(
        children: [
          // Main content centered on the screen.
          Center(
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                // Wrap SphereWidget with GestureDetector for double tap action.
                GestureDetector(
                  onDoubleTap: () {
                    // Navigate to SphereInventoryScreen on double tap.
                    Navigator.of(context).push(MaterialPageRoute(
                      builder: (context) => SphereInventoryPage(),
                    ));
                    print('Sphere widget double tapped!');
                  },
                  child: SizedBox(
                    height: 400,
                    child: SphereWidget(
                        imageUrl: 'assets/images/Rocky.jpeg',
                      level: sphereLevel ?? 1,
                      baseSize: 300,
                    ),
                  ),
                ),
                const SizedBox(height: 20),
                Text(
                  sphereName ?? 'Rocky',
                  style: theme.textTheme.displayLarge,
                ),
                Text(
              'Level: ${sphereLevel ?? 1}',
              style: theme.textTheme.titleLarge?.copyWith(fontSize: 20),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}