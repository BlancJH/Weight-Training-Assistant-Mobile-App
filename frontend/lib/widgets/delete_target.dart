import 'package:flutter/material.dart';

class DeleteTarget extends StatelessWidget {
  final Function(Map<String, dynamic> data) onAccept;

  const DeleteTarget({
    Key? key,
    required this.onAccept,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return DragTarget<Map<String, dynamic>>(
      onWillAccept: (data) => true,
      onAccept: (data) {
        onAccept(data);
      },
      builder: (context, candidateData, rejectedData) {
        return Center(
          child: Container(
            padding: EdgeInsets.all(16.0),
            decoration: BoxDecoration(
              shape: BoxShape.circle,
              color: candidateData.isNotEmpty ? Colors.redAccent : Colors.grey[300],
            ),
            child: Icon(
              Icons.delete,
              size: 40,
              color: Colors.white,
            ),
          ),
        );
      },
    );
  }
}
