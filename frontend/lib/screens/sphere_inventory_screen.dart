import 'package:flutter/material.dart';
import '../widgets/sphere_widget.dart';

class SphereInventoryPage extends StatelessWidget {
  // Example sphere inventory data.
  final List<Map<String, dynamic>> sphereInventory = [
    {
      'name': 'Rocky',
      'level': 3,
      'imageUrl': 'assets/images/Rocky.jpeg',
    },
    {
      'name': 'Flamy',
      'level': 2,
      'imageUrl': 'assets/images/Flamy.png',
    },
    {
      'name': 'Neo Core',
      'level': 5,
      'imageUrl': 'assets/images/NeoCore.png',
    },
    {
      'name': 'Neuro Orb',
      'level': 10,
      'imageUrl': 'assets/images/NeuroOrb.png',
    },
    // Add more sphere data as needed.
  ];

  SphereInventoryPage({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    // Use MediaQuery to get screen height; 50% will be used for the large sphere.
    final screenHeight = MediaQuery.of(context).size.height;
    final sphereWidgetHeight = screenHeight * 0.4;

    return Scaffold(
      appBar: AppBar(
        title: const Text('Sphere Inventory'),
      ),
      body: Column(
        children: [
          // sphere widget with fixed height.
          Container(
            height: sphereWidgetHeight,
            alignment: Alignment.center,
            child: SphereWidget(
              imageUrl: 'assets/images/Rocky.jpeg',
              level: 3, // You can update this value as needed.
              // Use 80% of the container height as the base size.
              baseSize: sphereWidgetHeight * 2,
            ),
          ),
          // Bottom half: Grid list of sphere cards.
          Expanded(
            child: Padding(
              padding: const EdgeInsets.all(16.0),
              child: GridView.builder(
                itemCount: sphereInventory.length,
                gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
                  crossAxisCount: 2, // Two cards per row.
                  crossAxisSpacing: 16,
                  mainAxisSpacing: 16,
                  childAspectRatio: 1,
                ),
                itemBuilder: (context, index) {
                  final sphere = sphereInventory[index];
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
                          // Use a smaller SphereWidget for each card.
                          SphereWidget(
                            imageUrl: sphere['imageUrl'],
                            level: sphere['level'],
                            baseSize: 100, // Fixed base size for cards.
                          ),
                          const SizedBox(height: 8),
                          Text(
                            sphere['name'],
                            style: Theme.of(context).textTheme.titleMedium,
                          ),
                          Text(
                            'Level: ${sphere['level']}',
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
