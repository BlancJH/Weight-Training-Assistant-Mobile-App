class Exercise {
  final int id;
  final String exerciseName;
  final String primaryMuscle;
  final String category;
  final String gifUrl;

  Exercise({
    required this.id,
    required this.exerciseName,
    required this.primaryMuscle,
    required this.category,
    required this.gifUrl,
  });

  factory Exercise.fromJson(Map<String, dynamic> json) {
    return Exercise(
      id: json['id'] as int,
      exerciseName: json['exerciseName'] as String? ?? '',
      primaryMuscle: json['primaryMuscle'] as String? ?? '',
      category: json['category'] as String? ?? '',
      gifUrl: json['gifUrl'] as String? ?? '',
    );
  }
}
