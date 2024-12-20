import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';

class GifWidget extends StatelessWidget {
  final String gifUrl; // URL or asset path for the GIF
  final String text; // Text to display on the right
  final String? optionalText; // Optional text below the main text
  final double width; // Width of the entire widget
  final double height; // Height of the widget
  
  const GifWidget({
    Key? key,
    required this.gifUrl,
    required this.text,
    this.optionalText,
    required this.width,
    required this.height,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Container(
      width: width, // Apply width at usage level
      height: height, // Apply height at usage level
      padding: const EdgeInsets.all(16.0),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // GIF Section
          Container(
            width: width * 0.3, // 30% of the widget's width
            height: height, // Match the height
            decoration: BoxDecoration(
              borderRadius: BorderRadius.circular(8.0),
              color: Colors.grey[200],
            ),
            child: ClipRRect(
              borderRadius: BorderRadius.circular(8.0),
              child: Image.network(
                gifUrl,
                fit: BoxFit.cover,
                errorBuilder: (context, error, stackTrace) {
                  return Center(
                    child: Icon(
                      Icons.broken_image,
                      color: Colors.grey,
                    ),
                  );
                },
              ),
            ),
          ),
          const SizedBox(width: 16.0), // Space between GIF and text

          // Text Section
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  text,
                  style: TextStyle(
                    fontSize: 16.0,
                    fontWeight: FontWeight.bold,
                  ),
                ),
                if (optionalText != null) ...[
                  const SizedBox(height: 8.0), // Spacing above optional text
                  Text(
                    optionalText!,
                    style: TextStyle(
                      fontSize: 14.0,
                      color: Colors.grey[700],
                    ),
                  ),
                ],
              ],
            ),
          ),
        ],
      ),
    );
  }
}