import 'package:flutter/material.dart';

class LockedOverlay extends StatelessWidget {
  const LockedOverlay({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Positioned.fill(
      child: Container(
        decoration: BoxDecoration(
          color: Colors.grey.withOpacity(0.6),
          shape: BoxShape.circle,
        ),
      ),
    );
  }
}
