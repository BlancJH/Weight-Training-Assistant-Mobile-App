import 'package:flutter/material.dart';
import 'package:frontend_1/screens/sphere_inventory_screen.dart';
import 'package:frontend_1/utils/design_utils.dart';
import '../widgets/sphere_widget.dart';
import '../utils/design_utils.dart';

class SpherePage extends StatefulWidget {
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
  _SpherePageState createState() => _SpherePageState();
}

class _SpherePageState extends State<SpherePage> {
  late String selectedImageUrl;
  late String selectedSphereName;
  late int selectedSphereLevel;

  @override
  void initState() {
    super.initState();
    // Initialise with defaults.
    selectedImageUrl = 'assets/images/Rocky.jpeg';
    selectedSphereName = widget.sphereName ?? 'Rocky';
    selectedSphereLevel = widget.sphereLevel ?? 1;
  }

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
                  onDoubleTap: () async {
                    // Navigate to SphereInventoryScreen and wait for the result.
                    final result = await Navigator.of(context).push(
                      MaterialPageRoute(
                        builder: (context) => SphereInventoryPage(),
                      ),
                    );
                    // If result is returned (not null), update the selected sphere.
                    if (result != null && result is Map<String, dynamic>) {
                      setState(() {
                        selectedImageUrl = result['imageUrl'];
                        selectedSphereName = result['name'];
                        selectedSphereLevel = result['level'] ?? 1;
                      });
                    }
                    print('Sphere widget double tapped!');
                  },
                  child: SizedBox(
                    height: 400,
                    child: SphereWidget(
                      imageUrl: selectedImageUrl,
                      level: selectedSphereLevel,
                      baseSize: 300,
                    ),
                  ),
                ),
                const SizedBox(height: 20),
                Text(
                  selectedSphereName,
                  style: theme.textTheme.displayLarge,
                ),
                Text(
                  'Level: $selectedSphereLevel',
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
