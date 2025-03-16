String getSphereImageUrl(String sphereName) {
  final normalizedName = sphereName.toLowerCase().replaceAll(' ', '');
  final path = 'assets/images/$normalizedName.png';
  print('Asset path: $path');
  return path;
}
