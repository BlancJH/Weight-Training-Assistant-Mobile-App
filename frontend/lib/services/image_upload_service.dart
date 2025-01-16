import 'dart:io';
import 'package:firebase_storage/firebase_storage.dart';
import '../utils/image_compressor.dart';

class PhotoService {
  final FirebaseStorage _storage = FirebaseStorage.instance;

  Future<String?> uploadPhoto(File imageFile, String filePath) async {
    try {
      // Optionally compress the image before uploading
      final compressedImage = await ImageUtils.compressImage(imageFile);

      final ref = _storage.ref().child(filePath);
      await ref.putFile(compressedImage ?? imageFile);
      return await ref.getDownloadURL(); // Return the photo's URL
    } catch (e) {
      print('Upload failed: $e');
      return null;
    }
  }
}
