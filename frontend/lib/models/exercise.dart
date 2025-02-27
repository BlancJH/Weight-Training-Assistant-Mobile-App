class Exercise {
  final int id;
  final String name;
  final String primaryMuscle;
  final String category;
  final String gifUrl;

  Exercise({
    required this.id,
    required this.name,
    required this.primaryMuscle,
    required this.category,
    required this.gifUrl,
  });

  factory Exercise.fromJson(Map<String, dynamic> json) {
    return Exercise(
      id: json['id'] as int,
      name: json['name'] as String? ?? '',
      primaryMuscle: json['primaryMuscle'] as String? ?? '',
      category: json['category'] as String? ?? '',
      gifUrl: json['gifUrl'] as String? ?? '',
    );
  }
}
