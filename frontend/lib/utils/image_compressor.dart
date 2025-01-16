import 'dart:io';
import 'package:flutter_image_compress/flutter_image_compress.dart';

class ImageUtils {
  static Future<File?> compressImage(File file, {int quality = 85}) async {
    final compressed = await FlutterImageCompress.compressAndGetFile(
      file.absolute.path,
      '${file.absolute.path}_compressed.jpg',
      quality: quality,
    );
    return compressed;
  }
}
