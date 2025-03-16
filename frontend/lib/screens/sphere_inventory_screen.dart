import 'package:flutter/material.dart';
import '../widgets/sphere_widget.dart';
import '../widgets/locked_overlay.dart';
import '../services/sphere_service.dart';
import '../utils/sphere_assets.dart';

class SphereInventoryPage extends StatefulWidget {
  const SphereInventoryPage({Key? key}) : super(key: key);

  @override
  _SphereInventoryPageState createState() => _SphereInventoryPageState();
}

class _SphereInventoryPageState extends State<SphereInventoryPage> {
  // Complete list of available spheres on the frontend.
  List<Map<String, dynamic>> allSpheres = [];

  // List of user-owned spheres fetched from the backend.
  List<Map<String, dynamic>> userOwnedSpheres = [];

  // The currently selected sphere from the inventory.
  late Map<String, dynamic> selectedSphere;
  // The last selected sphere that is owned by the user.
  late Map<String, dynamic> lastValidOwnedSphere;

  bool _isLoading = true;
  String? _errorMessage;

  final SphereService _sphereService = SphereService();

  @override
  void initState() {
    super.initState();
    _initializeSpheres();
  }

  Future<void> _initializeSpheres() async {
    try {
      // Fetch the complete list of spheres and user-owned spheres asynchronously.
      final fetchedAllSpheres = await _sphereService.fetchSpheresFromDatabase();
      final fetchedUserSpheres = await _sphereService.fetchSpheres();

      setState(() {
        allSpheres = List<Map<String, dynamic>>.from(fetchedAllSpheres);
        userOwnedSpheres = List<Map<String, dynamic>>.from(fetchedUserSpheres);

        // Set default selected sphere from the allSpheres list.
        if (allSpheres.isNotEmpty) {
          selectedSphere = allSpheres[0];
          // Ensure the imageUrl is up-to-date.
          selectedSphere['imageUrl'] = getSphereImageUrl(selectedSphere['name']);
          if (_isOwned(selectedSphere['name'])) {
            lastValidOwnedSphere = selectedSphere;
          } else {
            lastValidOwnedSphere = allSpheres.firstWhere(
              (s) => _isOwned(s['name']),
              orElse: () => selectedSphere,
            );
          }
        } else {
          selectedSphere = {
            'name': 'Rocky',
            'imageUrl': getSphereImageUrl('Rocky'),
            'id': 0,
          };
          lastValidOwnedSphere = selectedSphere;
        }
        _isLoading = false;
      });
    } catch (error) {
      print("Error fetching spheres: $error");
      setState(() {
        _errorMessage = error.toString();
        _isLoading = false;
        userOwnedSpheres = [
          {'name': 'Rocky', 'level': 1},
        ];
        if (allSpheres.isNotEmpty) {
          selectedSphere = allSpheres[0];
          lastValidOwnedSphere = allSpheres[0];
        } else {
          selectedSphere = {
            'name': 'Rocky',
            'imageUrl': getSphereImageUrl('Rocky'),
            'id': 0,
          };
          lastValidOwnedSphere = selectedSphere;
        }
      });
    }
  }

  Future<void> _updateRepresentator() async {
    try {
      if (selectedSphere.containsKey('id')) {
        await _sphereService.updateRepresentator(sphereId: selectedSphere['id'] as int);
        print("Representator updated successfully with sphere id: ${selectedSphere['id']}");
      } else {
        print("Selected sphere does not contain an 'id'.");
      }
    } catch (error) {
      print("Error updating representator: $error");
    }
  }

  bool _isOwned(String sphereName) {
    return userOwnedSpheres.any((owned) => owned['name'] == sphereName);
  }

  int _getLevel(String sphereName) {
    if (_isOwned(sphereName)) {
      return userOwnedSpheres.firstWhere((owned) => owned['name'] == sphereName)['level'] as int;
    }
    return 1; // Default level if not owned.
  }

  @override
  Widget build(BuildContext context) {
    if (_isLoading) {
      return Scaffold(
        appBar: AppBar(title: const Text('Sphere Inventory')),
        body: const Center(child: CircularProgressIndicator()),
      );
    }

    if (_errorMessage != null) {
      return Scaffold(
        appBar: AppBar(title: const Text('Sphere Inventory')),
        body: Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Text('Error: $_errorMessage'),
              const SizedBox(height: 16),
              ElevatedButton(
                onPressed: () {
                  setState(() {
                    _isLoading = true;
                    _errorMessage = null;
                  });
                  _initializeSpheres();
                },
                child: const Text('Retry'),
              ),
            ],
          ),
        ),
      );
    }

    final screenHeight = MediaQuery.of(context).size.height;
    final sphereWidgetHeight = screenHeight * 0.4;
    final theme = Theme.of(context);

    // Check if the selected sphere is owned.
    final bool selectedOwned = _isOwned(selectedSphere['name']);

    return WillPopScope(
      onWillPop: () async {
        // Prepare the return data including the level and dynamic image URL.
        Map<String, dynamic> returnSphere;
        if (!selectedOwned) {
          returnSphere = {
            'name': lastValidOwnedSphere['name'],
            'imageUrl': getSphereImageUrl(lastValidOwnedSphere['name']),
            'level': _getLevel(lastValidOwnedSphere['name']),
            'id': lastValidOwnedSphere['id'] ?? 0,
          };
        } else {
          await _updateRepresentator();
          returnSphere = {
            'name': selectedSphere['name'],
            'imageUrl': getSphereImageUrl(selectedSphere['name']),
            'level': _getLevel(selectedSphere['name']),
            'id': selectedSphere['id'] ?? 0,
          };
        }
        Navigator.pop(context, returnSphere);
        return false;
      },
      child: Scaffold(
        appBar: AppBar(
          title: const Text('Sphere Inventory'),
        ),
        body: Column(
          children: [
            // Top section: Large sphere widget showing the selected sphere.
            Container(
              height: sphereWidgetHeight,
              alignment: Alignment.center,
              child: Stack(
                alignment: Alignment.center,
                children: [
                  SphereWidget(
                    imageUrl: getSphereImageUrl(selectedSphere['name']),
                    baseSize: sphereWidgetHeight * 0.8,
                  ),
                  if (!selectedOwned) const LockedOverlay(),
                ],
              ),
            ),
            // Bottom section: Grid list of sphere cards.
            Expanded(
              child: Padding(
                padding: const EdgeInsets.all(16.0),
                child: GridView.builder(
                  itemCount: allSpheres.length,
                  gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
                    crossAxisCount: 2,
                    crossAxisSpacing: 16,
                    mainAxisSpacing: 16,
                    childAspectRatio: 1,
                  ),
                  itemBuilder: (context, index) {
                    final sphere = allSpheres[index];
                    final bool owned = _isOwned(sphere['name']);
                    final int level = owned ? _getLevel(sphere['name']) : 1;

                    return GestureDetector(
                      onTap: () {
                        setState(() {
                          selectedSphere = sphere;
                          // Ensure the image URL is updated based on the sphere name.
                          selectedSphere['imageUrl'] = getSphereImageUrl(sphere['name']);
                          if (owned) {
                            lastValidOwnedSphere = sphere;
                          }
                        });
                        print('Tapped on ${sphere['name']}');
                      },
                      child: Card(
                        elevation: 4,
                        shape: RoundedRectangleBorder(
                          borderRadius: BorderRadius.circular(16),
                        ),
                        child: Column(
                          mainAxisAlignment: MainAxisAlignment.center,
                          children: [
                            Stack(
                              alignment: Alignment.center,
                              children: [
                                ClipOval(
                                  child: Image.asset(
                                    getSphereImageUrl(sphere['name']),
                                    width: 100,
                                    height: 100,
                                    fit: BoxFit.cover,
                                  ),
                                ),
                                if (!owned) const LockedOverlay(),
                              ],
                            ),
                            const SizedBox(height: 8),
                            Text(
                              sphere['name'],
                              style: theme.textTheme.titleMedium,
                            ),
                            Text(
                              owned ? 'Level: $level' : 'Locked',
                              style: theme.textTheme.bodySmall,
                            ),
                          ],
                        ),
                      ),
                    );
                  },
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
