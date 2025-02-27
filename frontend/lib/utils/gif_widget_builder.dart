import 'package:flutter/material.dart';
import 'package:frontend_1/models/exercise_gif.dart';
import '../widgets/gif_widget.dart';

class GifWidgetBuilder extends StatelessWidget {
  final List<ExerciseGifModel> gifData;

  const GifWidgetBuilder({
    required this.gifData,
    Key? key,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Column(
      children: gifData
          .map((gif) => Padding(
                padding: const EdgeInsets.only(bottom: 16.0),
                child: GifWidget(
                  gifUrl: gif.gifUrl,
                  text: gif.text,
                  optionalText: gif.optionalText,
                  width: 300, // Set custom width
                  height: 150, // Set custom height
                ),
              ))
          .toList(),
    );
  }
}
