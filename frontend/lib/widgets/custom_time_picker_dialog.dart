import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:frontend_1/utils/design_utils.dart';

class CustomTimePickerDialog extends StatefulWidget {
  final int initialMinutes; // Initial value in minutes

  const CustomTimePickerDialog({Key? key, this.initialMinutes = 0}) : super(key: key);

  @override
  _CustomTimePickerDialogState createState() => _CustomTimePickerDialogState();
}

class _CustomTimePickerDialogState extends State<CustomTimePickerDialog> {
  int selectedHour = 0;
  int selectedMinute = 0;

  @override
  void initState() {
    super.initState();
    selectedHour = widget.initialMinutes ~/ 60;
    selectedMinute = widget.initialMinutes % 60;
  }

  @override
  Widget build(BuildContext context) {
    return AlertDialog(
      title: const Text('Select Workout Duration'),
      content: Row(
        children: [
          // Hour Picker
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.center,
              mainAxisSize: MainAxisSize.min,
              children: [
                const Text('Hours'),
                SizedBox(
                  height: 100,
                  child: CupertinoPicker(
                    scrollController: FixedExtentScrollController(initialItem: selectedHour),
                    itemExtent: 40.0,
                    onSelectedItemChanged: (value) {
                      setState(() {
                        selectedHour = value;
                      });
                    },
                    children: List.generate(24, (index) => Center(child: Text('$index h'))),
                  ),
                ),
              ],
            ),
          ),
          // Minute Picker
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.center,
              mainAxisSize: MainAxisSize.min,
              children: [
                const Text('Minutes'),
                SizedBox(
                  height: 100,
                  child: CupertinoPicker(
                    scrollController: FixedExtentScrollController(initialItem: selectedMinute),
                    itemExtent: 40.0,
                    onSelectedItemChanged: (value) {
                      setState(() {
                        selectedMinute = value;
                      });
                    },
                    children: List.generate(60, (index) => Center(child: Text('$index min'))),
                  ),
                ),
              ],
            ),
          ),
        ],
      ),
      actions: [
        TextButton(
          style: TextButton.styleFrom(
          foregroundColor: primaryTextColor,
        ),
          onPressed: () => Navigator.of(context).pop(), // Close dialog without saving
          child: const Text('Cancel'),
        ),
        TextButton(
          style: TextButton.styleFrom(
          foregroundColor: primaryTextColor,
          ),
          onPressed: () => Navigator.of(context).pop((selectedHour * 60) + selectedMinute),
          child: const Text('OK'),
        ),
      ],
    );
  }
}
