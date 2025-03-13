import 'package:flutter/material.dart';

class SphereWidget extends StatefulWidget {
  final String imageUrl;
  final int level; 
  final double baseSize;

  const SphereWidget({
    required this.imageUrl, // Now can be asset path
    this.level = 1,
    this.baseSize = 300,
    Key? key,
  }) : super(key: key);

  @override
  State<SphereWidget> createState() => _SphereWidgetState();
}

class _SphereWidgetState extends State<SphereWidget> {
  double _dragAmount = 0;

  void _onDragUpdate(DragUpdateDetails details) {
    setState(() {
      // Update drag amount to simulate light movement (left to right)
      _dragAmount += details.primaryDelta! * 0.005;
      _dragAmount = _dragAmount.clamp(-1.0, 1.0); // Limit drag range
    });
  }

  @override
  Widget build(BuildContext context) {

    // Ensure level is at least 1
    final int effectiveLevel = widget.level > 0 ? widget.level : 1;
    // Calculate size: for each level above 1, 10% of maximum size.
    final double computedSize = widget.baseSize * (effectiveLevel * 0.1);

    return GestureDetector(
      onHorizontalDragUpdate: _onDragUpdate,
      child: Container(
        width: computedSize,
        height: computedSize,
        decoration: BoxDecoration(
          shape: BoxShape.circle,
          boxShadow: [
            BoxShadow(
              color: Colors.cyanAccent.withOpacity(0.6), // Outer neon glow
              blurRadius: 30,
              spreadRadius: 10,
            )
          ],
        ),
        child: Stack(
          alignment: Alignment.center,
          children: [
            // Optional slight rotation for interactive feel
            Transform.rotate(
              angle: _dragAmount * 0.3, // Rotate slightly on drag
              child: ClipOval(
                child: Image.asset(
                  widget.imageUrl, // Local asset image
                  width: computedSize,
                  height: computedSize,
                  fit: BoxFit.cover,
                ),
              ),
            ),

            // Lighting effect
            Positioned.fill(
              child: ShaderMask(
                shaderCallback: (Rect bounds) {
                  return RadialGradient(
                    center: Alignment(0.0 + _dragAmount, -0.3), // Light follows drag
                    radius: 0.8, // Smoother transition
                    colors: [
                      Colors.transparent,
                      Colors.black.withOpacity(0.5), // Darker shadow
                    ],
                    stops: [0.4, 1.0],
                  ).createShader(bounds);
                },
                blendMode: BlendMode.darken,
                child: Container(color: Colors.transparent),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
