import 'package:flutter/material.dart';

class SphereWidget extends StatefulWidget {
  final String imageUrl;
  final double size;

  const SphereWidget({
    required this.imageUrl, // Now can be asset path
    this.size = 300,
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
    return GestureDetector(
      onHorizontalDragUpdate: _onDragUpdate,
      child: Container(
        width: widget.size,
        height: widget.size,
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
                  width: widget.size,
                  height: widget.size,
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
