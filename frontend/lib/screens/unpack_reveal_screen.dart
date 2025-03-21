import 'package:flutter/material.dart';
import '../utils/sphere_assets.dart';
import '../utils/design_utils.dart';

class UnpackRevealScreen extends StatefulWidget {
  final List<dynamic> spheres; // List of maps from the API

  const UnpackRevealScreen({Key? key, required this.spheres})
      : super(key: key);

  @override
  _UnpackRevealScreenState createState() =>
      _UnpackRevealScreenState();
}

class _UnpackRevealScreenState
    extends State<UnpackRevealScreen> {
  int currentIndex = 0;

  void _showNext() {
    if (currentIndex < widget.spheres.length - 1) {
      setState(() {
        currentIndex++;
      });
    } else {
      // When done, navigate back or perform another final action.
      Navigator.of(context).pop();
    }
  }

  @override
  Widget build(BuildContext context) {
    // Extract sphere name from the current map.
    final Map currentSphere = widget.spheres[currentIndex];
    final String sphereName = currentSphere['sphereName'] ?? 'default';
    final String imageUrl = getSphereImageUrl(sphereName);

    return Scaffold(
      appBar: AppBar(
        title: Text("Your Sphere Pack"),
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            // AnimatedSwitcher to provide a smooth transition between sphere images.
            AnimatedSwitcher(
              duration: Duration(milliseconds: 500),
              child: Image.asset(
                imageUrl,
                key: ValueKey(imageUrl),
                height: 300,
              ),
            ),
            SizedBox(height: 20),
            // Display the sphere name.
            AnimatedSwitcher(
              duration: Duration(milliseconds: 500),
              child: Text(
                sphereName,
                key: ValueKey(sphereName),
                style: Theme.of(context).textTheme.displayLarge,
              ),
            ),
            SizedBox(height: 20),
            // Progress indicator.
            Text(
              'New Spheres ${currentIndex + 1} / ${widget.spheres.length}',
              style: Theme.of(context).textTheme.titleLarge?.copyWith(fontSize: 20),
            ),
            SizedBox(height: 20),
            // "Next" or "Done" button.
            ElevatedButton(
              onPressed: _showNext,
              child: Text(
                  currentIndex < widget.spheres.length - 1 ? "Next" : "Done"),
            ),
          ],
        ),
      ),
    );
  }
}
