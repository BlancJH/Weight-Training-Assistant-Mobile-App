import 'package:flutter/material.dart';
import '../widgets/sphere_widget.dart';
import '../widgets/locked_overlay.dart'; // Make sure to import the LockedOverlay widget.

class SphereInventoryPage extends StatefulWidget {
  const SphereInventoryPage({Key? key}) : super(key: key);

  @override
  _SphereInventoryPageState createState() => _SphereInventoryPageState();
}

class _SphereInventoryPageState extends State<SphereInventoryPage> {
  // Complete list of available spheres on the frontend.
  final List<Map<String, dynamic>> allSpheres = [
    {
      'name': 'Rocky',
      'imageUrl': 'assets/images/Rocky.jpeg',
    },
    {
      'name': 'Flamy',
      'imageUrl': 'assets/images/Flamy.png',
    },
    {
      'name': 'Neo Core',
      'imageUrl': 'assets/images/NeoCore.png',
    },
    {
      'name': 'Neuro Orb',
      'imageUrl': 'assets/images/NeuroOrb.png',
    },
    {
      'name': 'Abyss',
      'imageUrl': 'assets/images/Abyss.png',
    },
    // Add more spheres as needed.
  ];

  // Spheres that the user owns (fetched from backend).
  // Each entry contains the sphere name and its level.
  final List<Map<String, dynamic>> userOwnedSpheres = [
    {
      'name': 'Rocky',
      'level': 3,
    },
    {
      'name': 'Neuro Orb',
      'level': 10,
    },
  ];

  // The currently selected sphere from the inventory.
  late Map<String, dynamic> selectedSphere;
  // The last selected sphere that is owned by the user.
  late Map<String, dynamic> lastValidOwnedSphere;

  @override
  void initState() {
    super.initState();
    // Set default selected sphere to the first one.
    selectedSphere = allSpheres[0];
    // Initialise lastValidOwnedSphere to the first owned sphere.
    if (_isOwned(selectedSphere['name'])) {
      lastValidOwnedSphere = selectedSphere;
    } else {
      // If first sphere is locked, find the first owned sphere.
      lastValidOwnedSphere = allSpheres.firstWhere((s) => _isOwned(s['name']));
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
    // Use MediaQuery to get screen height; 40% for the large sphere widget.
    final screenHeight = MediaQuery.of(context).size.height;
    final sphereWidgetHeight = screenHeight * 0.4;
    final theme = Theme.of(context);

    // Check if the selected sphere is owned.
    final bool selectedOwned = _isOwned(selectedSphere['name']);

    return WillPopScope(
      onWillPop: () async {
        // When exiting, if the selected sphere is not owned, return the last valid (owned) sphere.
        if (!selectedOwned) {
          Navigator.pop(context, lastValidOwnedSphere);
        } else {
          Navigator.pop(context, selectedSphere);
        }
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
                  // Display the selected sphere's image.
                  SphereWidget(
                    imageUrl: selectedSphere['imageUrl'],
                    baseSize: sphereWidgetHeight * 0.8, // Fixed size; level is not passed.
                  ),
                  // If the selected sphere is not owned, overlay the locked cover.
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
                    crossAxisCount: 2, // Two cards per row.
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
                            // Render sphere image in a circular clip.
                            Stack(
                              alignment: Alignment.center,
                              children: [
                                ClipOval(
                                  child: Image.asset(
                                    sphere['imageUrl'],
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
