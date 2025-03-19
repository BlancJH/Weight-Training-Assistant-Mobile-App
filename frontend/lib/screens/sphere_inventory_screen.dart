import 'package:flutter/material.dart';
import '../widgets/sphere_widget.dart';
import '../widgets/locked_overlay.dart';
import '../services/sphere_service.dart';
import '../utils/sphere_assets.dart';
import '../utils/design_utils.dart';

class SphereInventoryPage extends StatefulWidget {
  const SphereInventoryPage({Key? key}) : super(key: key);

  @override
  _SphereInventoryPageState createState() => _SphereInventoryPageState();
}

class _SphereInventoryPageState extends State<SphereInventoryPage> {
  // Complete list of available spheres from the backend.
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

        // Set default selected sphere from the full list.
        if (allSpheres.isNotEmpty) {
          // Use the first sphere in the full list as default.
          selectedSphere = allSpheres[0];
          selectedSphere['imageUrl'] = getSphereImageUrl(selectedSphere['name']);

          // If the default sphere is owned, mark it as the last valid owned sphere.
          if (_isOwned(selectedSphere['name'])) {
            lastValidOwnedSphere = selectedSphere;
          } else {
            // Otherwise, try to find the first sphere that is owned.
            lastValidOwnedSphere = allSpheres.firstWhere(
              (s) => _isOwned(s['name']),
              orElse: () => selectedSphere,
            );
          }
        } else {
          // Fallback to a default sphere if the list is empty.
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
        // Provide fallback data for userOwnedSpheres.
        userOwnedSpheres = [
          {'name': 'Rocky', 'level': 1, 'quantity': 1},
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

  // Checks if the sphere (by name) is in the list of user-owned spheres.
  bool _isOwned(String sphereName) {
    return userOwnedSpheres.any((owned) => owned['name'] == sphereName);
  }

  int _getLevel(String sphereName) {
    if (_isOwned(sphereName)) {
      return userOwnedSpheres.firstWhere((owned) => owned['name'] == sphereName)['level'] as int;
    }
    return 1; // Default level if not owned.
  }

  int _getQuantity(String sphereName) {
    if (_isOwned(sphereName)) {
      return userOwnedSpheres.firstWhere((owned) => owned['name'] == sphereName)['quantity'] as int;
    }
    return 1; // Default quantity if not owned.
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

    // Check if the currently selected sphere is owned.
    final bool selectedOwned = _isOwned(selectedSphere['name']);

    return WillPopScope(
      onWillPop: () async {
        // When exiting:
        // - If the selected sphere is owned, update representator and return updated data.
        // - Otherwise, return the last valid (owned) sphere's updated data.
        Map<String, dynamic> returnSphere;
        if (selectedOwned) {
          await _updateRepresentator();
          returnSphere = {
            'name': selectedSphere['name'],
            'imageUrl': getSphereImageUrl(selectedSphere['name']),
            'level': _getLevel(selectedSphere['name']),
            'quantity': _getQuantity(selectedSphere['name']),
            'id': selectedSphere['id'] ?? 0,
          };
        } else {
          returnSphere = {
            'name': lastValidOwnedSphere['name'],
            'imageUrl': getSphereImageUrl(lastValidOwnedSphere['name']),
            'level': _getLevel(lastValidOwnedSphere['name']),
            'quantity': _getQuantity(lastValidOwnedSphere['name']),
            'id': lastValidOwnedSphere['id'] ?? 0,
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
                  // Overlay the locked cover if the selected sphere is not owned.
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
                    final int quantity = owned ? _getQuantity(sphere['name']) : 1;

                    return GestureDetector(
                      onTap: () {
                        setState(() {
                          selectedSphere = sphere;
                          // Update the image URL based on sphere name.
                          selectedSphere['imageUrl'] = getSphereImageUrl(sphere['name']);
                          // If the sphere is owned, update the last valid selection.
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
                                // If not owned, display a grey overlay.
                                if (!owned) const LockedOverlay(),
                                // Overlay quantity count at bottom right.
                                if (owned) 
                                Positioned(
                                  bottom: 4,
                                  right: 4,
                                  child: Container(
                                    padding: const EdgeInsets.all(4),
                                    decoration: BoxDecoration(
                                      shape: BoxShape.circle,
                                      color: buttonColor, // Use the theme's button color.
                                    ),
                                    child: Text(
                                      quantity.toString(),
                                      style: TextStyle(
                                        color: primaryTextColor ?? Colors.white,
                                        fontSize: 12,
                                      ),
                                    ),
                                  ),
                                ),
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
