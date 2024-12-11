class Validators {
  // Helper: Check if a field is required
  static String? _checkRequired(String? value, String fieldName) {
    if (value == null || value.trim().isEmpty) {
      return '$fieldName is required';
    }
    return null;
  }

  // Validate required field with no spaces in the middle
  static String? validateRequired(String? value, String fieldName) {
    final requiredError = _checkRequired(value, fieldName);
    if (requiredError != null) return requiredError;

    if (value!.contains(' ')) {
      return '$fieldName cannot contain spaces in the middle';
    }
    return null;
  }

  // Validate email format
  static String? validateEmail(String? value) {
    final requiredError = _checkRequired(value, 'Email');
    if (requiredError != null) return requiredError;

    if (value!.contains(' ')) {
      return 'Email cannot contain spaces';
    }

    final emailRegex = RegExp(r'^[^@]+@[^@]+\.[^@]+$');
    if (!emailRegex.hasMatch(value)) {
      return 'Enter a valid email address';
    }
    return null;
  }

  // Validate password strength
  static String? validatePassword(String? value) {
    final requiredError = _checkRequired(value, 'Password');
    if (requiredError != null) return requiredError;

    if (value!.length < 6) {
      return 'Password must be at least 6 characters long';
    }
    return null;
  }

  // Validate matching fields
  static String? validateMatch(String? value, String? matchValue, String fieldName) {
    final requiredError = _checkRequired(value, fieldName);
    if (requiredError != null) return requiredError;

    if (value != matchValue) {
      return '$fieldName does not match';
    }
    return null;
  }
}
