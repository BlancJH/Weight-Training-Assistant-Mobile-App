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

  late Map<String, dynamic> selectedSphere;

  @override
  void initState() {
    super.initState();
    // Set default selected sphere to the first one.
    selectedSphere = allSpheres[0];
  }

  // Wrap the Scaffold in a WillPopScope to intercept back navigation.
  @override
  Widget build(BuildContext context) {
    final screenHeight = MediaQuery.of(context).size.height;
    final sphereWidgetHeight = screenHeight * 0.4;
    final theme = Theme.of(context);

    // Check if the selected sphere is owned.
    final bool selectedOwned = userOwnedSpheres
        .any((owned) => owned['name'] == selectedSphere['name']);

    return WillPopScope(
      onWillPop: () async {
        // Return the selected sphere data when navigating back.
        Navigator.pop(context, selectedSphere);
        // Prevent the default pop because we've already handled it.
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
                  // If not owned, overlay the locked cover.
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
                    // Check if the user owns this sphere.
                    final ownedList = userOwnedSpheres
                        .where((owned) => owned['name'] == sphere['name'])
                        .toList();
                    final bool owned = ownedList.isNotEmpty;
                    // For display in the grid, show level if owned; default to 1 otherwise.
                    final int level = owned ? ownedList.first['level'] : 1;

                    return GestureDetector(
                      onTap: () {
                        setState(() {
                          selectedSphere = sphere;
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
                            // Render sphere image in a circular clip with an overlay if not owned.
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
