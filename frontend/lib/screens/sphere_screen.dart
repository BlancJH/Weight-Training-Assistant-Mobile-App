import 'package:flutter/material.dart';
import 'package:frontend_1/screens/sphere_inventory_screen.dart';
import 'package:frontend_1/utils/design_utils.dart';
import '../widgets/sphere_widget.dart';
import '../utils/design_utils.dart';
import '../services/sphere_service.dart'; // Import your service
import '../utils/sphere_assets.dart';

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
  String selectedImageUrl = 'assets/images/rocky.png';
  String selectedSphereName = 'Rocky';
  int selectedSphereLevel = 1;
  int selectedSphereQuantity = 1;
  bool _isLoading = true; // Track loading state

  final SphereService _sphereService = SphereService();

  @override
  void initState() {
    super.initState();
    _fetchRepresentatorData();
  }

  Future<void> _fetchRepresentatorData() async {
    try {
      final data = await _sphereService.fetchRepresentator();
      setState(() {
        selectedSphereName = data['sphereName'] ?? selectedSphereName; // Update name
        selectedImageUrl = getSphereImageUrl(selectedSphereName); // Compute image dynamically
        selectedSphereLevel = data['level'] ?? selectedSphereLevel;
        selectedSphereQuantity = data['quantity'] ?? selectedSphereQuantity;
        _isLoading = false; // Set loading state to false
      });
      print('Fetched representator data: $data');
    } catch (error) {
      print('Error fetching representator data: $error');
      setState(() {
        _isLoading = false; // Ensure loading stops even if there’s an error
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    if (_isLoading) {
      return Scaffold(
        body: Center(child: CircularProgressIndicator()), // Show loading indicator
      );
    }

    final theme = Theme.of(context);
    return Scaffold(
      body: Stack(
        children: [
          Center(
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                GestureDetector(
                  onDoubleTap: () async {
                    final result = await Navigator.of(context).push(
                      MaterialPageRoute(
                        builder: (context) => SphereInventoryPage(),
                      ),
                    );
                    if (result != null && result is Map<String, dynamic>) {
                      setState(() {
                        selectedSphereName = result['name'];
                        selectedImageUrl = getSphereImageUrl(selectedSphereName);
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
          if (selectedSphereQuantity >= 5)
            Positioned(
              top: 40,
              left: 0,
              right: 0,
              child: Center(
                child: IconButton(
                  icon: const Icon(Icons.arrow_upward, size: 32, color: buttonColor),
                  onPressed: () {
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
