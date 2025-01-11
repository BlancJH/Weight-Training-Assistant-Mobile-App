import 'package:flutter/material.dart';

class UnitToggle extends StatelessWidget {
  final List<String> units; // List of unit options
  final String activeUnit; // Currently selected unit
  final ValueChanged<String> onUnitChanged; // Callback when unit is selected

  const UnitToggle({
    required this.units, // Units to toggle between
    required this.activeUnit, // Active unit for bold display
    required this.onUnitChanged, // Callback on change
    Key? key,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Row(
      mainAxisSize: MainAxisSize.min,
      children: units.map((unit) {
        return GestureDetector(
          onTap: () => onUnitChanged(unit),
          child: Row(
            children: [
              Text(
                unit,
                style: TextStyle(
                  fontSize: 16.0,
                  fontWeight: unit == activeUnit ? FontWeight.bold : FontWeight.normal,
                  color: unit == activeUnit ? Colors.blue : Colors.black,
                ),
              ),
              if (unit != units.last) const Text(' | '), // Separator for units
            ],
          ),
        );
      }).toList(),
    );
  }
}
