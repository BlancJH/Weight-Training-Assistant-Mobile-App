import 'package:flutter/material.dart';

class SphereWidget extends StatefulWidget {
  final String imageUrl;
  final int? level; // Nullable level parameter.
  final double baseSize;

  const SphereWidget({
    required this.imageUrl,
    this.level, // If not provided, level is null.
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
      // Update drag amount to simulate light movement.
      _dragAmount += details.primaryDelta! * 0.005;
      _dragAmount = _dragAmount.clamp(-1.0, 1.0);
    });
  }

  @override
  Widget build(BuildContext context) {
    // Use widget.level if provided; otherwise treat as level 10.
    final int effectiveLevel = widget.level ?? 10;
    // Ensure level is at least 1.
    final int validLevel = effectiveLevel < 1 ? 1 : effectiveLevel;
    // Calculate size: level 10 corresponds to the full baseSize.
    final double computedSize = widget.baseSize * (validLevel * 0.1);

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
            ),
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
