class ExerciseGifModel {
  final String gifUrl;
  final String text;
  final String? optionalText;

  ExerciseGifModel({
    required this.gifUrl,
    required this.text,
    this.optionalText,
  });

  factory ExerciseGifModel.fromJson(Map<String, dynamic> json) {
    return ExerciseGifModel(
      gifUrl: json['gifUrl'],
      text: json['text'],
      optionalText: json['optionalText'],
    );
  }
}
