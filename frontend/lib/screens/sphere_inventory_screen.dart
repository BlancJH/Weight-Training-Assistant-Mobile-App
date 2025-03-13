import 'package:flutter/material.dart';
import '../widgets/sphere_widget.dart';

class SphereInventoryPage extends StatelessWidget {
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

  SphereInventoryPage({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    // Use MediaQuery to get screen height; 40% will be used for the large sphere widget.
    final screenHeight = MediaQuery.of(context).size.height;
    final sphereWidgetHeight = screenHeight * 0.4;

    return Scaffold(
      appBar: AppBar(
        title: const Text('Sphere Inventory'),
      ),
      body: Column(
        children: [
          // Top section: Large sphere widget.
          Container(
            height: sphereWidgetHeight,
            alignment: Alignment.center,
            child: SphereWidget(
              imageUrl: 'assets/images/Rocky.jpeg',
              level: 3,
              baseSize: sphereWidgetHeight * 1.5,
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
                  // If owned, retrieve its level; otherwise, default level to 0.
                  final int level = owned ? ownedList.first['level'] : 1;

                  return GestureDetector(
                    onTap: () {
                      // TODO: Add sphere card tap action (e.g., navigate to details).
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
                              if (!owned)
                                Positioned.fill(
                                  child: Container(
                                    decoration: BoxDecoration(
                                      color: Colors.grey.withOpacity(0.6),
                                      shape: BoxShape.circle,
                                    ),
                                  ),
                                ),
                            ],
                          ),
                          const SizedBox(height: 8),
                          Text(
                            sphere['name'],
                            style: Theme.of(context).textTheme.titleMedium,
                          ),
                          Text(
                            owned ? 'Level: $level' : 'Locked',
                            style: Theme.of(context).textTheme.bodySmall,
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
    );
  }
}
