import 'package:flutter/material.dart';
import 'package:frontend_1/screens/sphere_inventory_screen.dart';
import 'package:frontend_1/utils/design_utils.dart';
import '../widgets/sphere_widget.dart';
import '../utils/design_utils.dart';
import '../services/sphere_service.dart'; // Import your service

class SpherePage extends StatefulWidget {
  final String? username;
  final String? profileUrl;
  final String? sphereName;
  final int? sphereLevel;
  final int? sphereQuantity;

  const SpherePage({
    this.username,
    this.profileUrl,
    this.sphereName,
    this.sphereLevel,
    this.sphereQuantity,
    Key? key,
  }) : super(key: key);

  @override
  _SpherePageState createState() => _SpherePageState();
}

class _SpherePageState extends State<SpherePage> {
  late String selectedImageUrl;
  late String selectedSphereName;
  late int selectedSphereLevel;
  int selectedSphereQuantity = 1;

  final SphereService _sphereService = SphereService();

  @override
  void initState() {
    super.initState();
    // Initialise with default values.
    selectedImageUrl = 'assets/images/Rocky.jpeg';
    selectedSphereName = widget.sphereName ?? 'Rocky';
    selectedSphereLevel = widget.sphereLevel ?? 1;
    selectedSphereQuantity = 1;

    // Fetch representator data from backend when the widget initializes.
    _fetchRepresentatorData();
  }

  Future<void> _fetchRepresentatorData() async {
    try {
      final data = await _sphereService.fetchRepresentator();
      setState(() {
        selectedImageUrl = data['imageUrl'] ?? selectedImageUrl;
        selectedSphereName = data['name'] ?? selectedSphereName;
        selectedSphereLevel = data['level'] ?? selectedSphereLevel;
        selectedSphereQuantity = data['quantity'] ?? selectedSphereQuantity;
      });
      print('Fetched representator data: $data');
    } catch (error) {
      print('Error fetching representator data: $error');
    }
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
                        selectedSphereLevel = result['level'] ?? selectedSphereLevel;
                        selectedSphereQuantity = result['quantity'] ?? selectedSphereQuantity;
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
          // Add an upper arrow button if the selected sphere's quantity is greater than 5.
          if (selectedSphereQuantity >= 5)
            Positioned(
              top: 40, // adjust vertical position as needed.
              left: 0,
              right: 0,
              child: Center(
                child: IconButton(
                  icon: const Icon(Icons.arrow_upward, size: 32, color: buttonColor),
                  onPressed: () {
                    // Add functionality as needed. For example, you might want to scroll the view up.
                    print('Upper arrow pressed!');
                  },
                ),
              ),
            ),
        ],
      ),
    );
  }
}
