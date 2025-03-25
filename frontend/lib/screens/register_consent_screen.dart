import 'package:flutter/material.dart';

class ConsentDocumentScreen extends StatelessWidget {
  final String title;
  final String documentContent;

  const ConsentDocumentScreen({
    Key? key,
    required this.title,
    required this.documentContent,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(title),
        // The AppBar automatically provides a back button.
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16.0),
        child: Text(
          documentContent,
          style: TextStyle(fontSize: 16.0, height: 1.5),
        ),
      ),
    );
  }
}
