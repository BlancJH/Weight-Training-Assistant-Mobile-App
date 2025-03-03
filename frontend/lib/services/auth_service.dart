import 'dart:convert'; // For JSON encoding/decoding
import 'package:http/http.dart' as http;
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:jwt_decoder/jwt_decoder.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:google_sign_in/google_sign_in.dart';
import 'package:sign_in_with_apple/sign_in_with_apple.dart';

class AuthService {
  // Base URL for your backend
  final String? _baseUrl = dotenv.env['BACKEND_BASE_URL']?? "http://default-url.com";
  final FlutterSecureStorage _storage = FlutterSecureStorage();

  // Register User
  Future<http.Response> registerUser({
    required String username,
    required String email,
    required String password,
  }) async {
    final url = '$_baseUrl/auth/register';

    try {
      final body = json.encode({
        'username': username,
        'email': email,
        'password': password,
      });

      final response = await http.post(
        Uri.parse(url),
        headers: {'Content-Type': 'application/json'},
        body: body,
      );

      return response;
    } catch (e) {
      print('Error during registration: $e');
      throw Exception('Failed to register user: $e');
    }
  }

  // Login User
  Future<String> loginUser({
    required String email,
    required String password,
  }) async {
    final url = '$_baseUrl/auth/login';

    try {
      final body = json.encode({
        'email': email,
        'password': password,
      });

      final response = await http.post(
        Uri.parse(url),
        headers: {'Content-Type': 'application/json'},
        body: body,
      );

      if (response.statusCode == 200) {
        final data = json.decode(response.body);
        final token = data['token'];

        // Save token securely
        await _storage.write(key: 'jwt', value: token);

        return token; // Return the token
      } else {
        final error = json.decode(response.body);
        throw Exception('Login failed: ${error['message'] ?? response.body}');
      }
    } catch (e) {
      print('Error during login: $e');
      throw Exception('Failed to login: $e');
    }
  }

  // Retrieve Token
  Future<String?> getToken() async {
    final token = await _storage.read(key: 'jwt');
    print("Retrieved JWT: $token");// Get the token from secure storage
    return token;
  }

  // Logout User
  Future<void> logoutUser() async {
    await _storage.delete(key: 'jwt'); // Delete the token from secure storage
  }

  /// Decode JWT and extract user data
  Future<Map<String, dynamic>?> decodeToken() async {
    final token = await getToken();
    if (token != null) {
      return JwtDecoder.decode(token); // Decodes the JWT
    }
    return null; // Return null if no token exists
  }

  // Google auth
  Future<void> handleGoogleSignIn() async {
    final GoogleSignIn googleSignIn = GoogleSignIn(
      scopes: ['email', 'profile'],
    );

    try {
      final GoogleSignInAccount? googleUser = await googleSignIn.signIn();
      if (googleUser == null) {
        throw Exception("Google Sign-In cancelled");
      }

      final GoogleSignInAuthentication googleAuth = await googleUser.authentication;
      final String? idToken = googleAuth.idToken;

      if (idToken == null) {
        throw Exception("Failed to get Google ID Token");
      }

      // Send ID token to backend for verification & login
      await _loginWithOAuthProvider('google', idToken);
    } catch (e) {
      print('Google Sign-In Error: $e');
      throw Exception("Failed to sign in with Google: $e");
    }
  }

  Future<void> handleAppleSignIn() async {
    try {
      final AuthorizationCredentialAppleID credential = await SignInWithApple.getAppleIDCredential(
        scopes: [
          AppleIDAuthorizationScopes.email,
          AppleIDAuthorizationScopes.fullName,
        ],
      );

      final String idToken = credential.identityToken ?? '';
      if (idToken.isEmpty) {
        throw Exception("Failed to get Apple ID Token");
      }

      // Send ID token to backend for verification & login
      await _loginWithOAuthProvider('apple', idToken);
    } catch (e) {
      print('Apple Sign-In Error: $e');
      throw Exception("Failed to sign in with Apple: $e");
    }
  }

  Future<void> _loginWithOAuthProvider(String provider, String idToken) async {
    final url = '$_baseUrl/auth/oauth/login';

    final body = json.encode({
      'provider': provider,
      'idToken': idToken,
    });

    final response = await http.post(
      Uri.parse(url),
      headers: {'Content-Type': 'application/json'},
      body: body,
    );

    if (response.statusCode == 200) {
      final data = json.decode(response.body);
      final token = data['token'];

      await _storage.write(key: 'jwt', value: token);
      print("OAuth login successful. JWT stored.");
    } else {
      final error = json.decode(response.body);
      throw Exception('OAuth login failed: ${error['message'] ?? response.body}');
    }
  }

}
