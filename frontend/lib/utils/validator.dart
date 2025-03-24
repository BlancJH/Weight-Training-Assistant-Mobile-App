class Validators {
  // Generic helper: Check if input meets conditions
  static String? _validate(
    String? value, // Input value
    String fieldName, // Field name for error messages
    {bool required = true, // Is the field required?
    bool noSpaces = false, // Disallow spaces?
    int? minLength, // Minimum length
    bool Function(String)? customCondition, // Additional condition
    String? customErrorMessage, // Error message for custom condition

    }) {
    // Check required
    if (required && (value == null || value.trim().isEmpty)) {
      return '$fieldName is required';
    }

    // If empty, no further checks needed
    if (value == null || value.isEmpty) return null;

    // Check for spaces
    if (noSpaces && value.contains(' ')) {
      return '$fieldName cannot contain spaces';
    }

    // Check minimum length
    if (minLength != null && value.length < minLength) {
      return '$fieldName must be at least $minLength characters long';
    }

    // Check custom condition
    if (customCondition != null && !customCondition(value)) {
      return customErrorMessage ?? '$fieldName is invalid';
    }

    return null;
  }

  // Validate required field with no spaces
  static String? validateRequired(String? value, String fieldName) {
    return _validate(value, fieldName, noSpaces: true);
  }

  // Validate email format
  static String? validateEmail(String? value) {
    final emailRegex = RegExp(r'^[^@]+@[^@]+\.[^@]+$');
    return _validate(
      value,
      'Email',
      noSpaces: true,
      customCondition: (val) => emailRegex.hasMatch(val),
      customErrorMessage: 'Enter a valid email address',
    );
  }

  // Validate password strength
  static String? validatePassword(String? value) {
    if (value == null || value.isEmpty) {
      return 'Password is required';
    }

    List<String> errors = [];

    if (value.length < 8) {
      errors.add('Password must be at least 8 characters long.');
    }
    if (!RegExp(r'(?=.*[A-Z])').hasMatch(value)) {
      errors.add('Password must contain at least one uppercase letter.');
    }
    if (!RegExp(r'(?=.*[a-z])').hasMatch(value)) {
      errors.add('Password must contain at least one lowercase letter.');
    }
    if (!RegExp(r'(?=.*\d)').hasMatch(value)) {
      errors.add('Password must contain at least one digit.');
    }
    if (!RegExp(r'(?=.*[^A-Za-z0-9])').hasMatch(value)) {
      errors.add('Password must contain at least one special character.');
    }

    return errors.isEmpty ? null : errors.join('\n');
  }

  // Validate matching fields
  static String? validateMatch(
    String? value,
    String? matchValue,
    String fieldName,
  ) {
    final error = _validate(value, fieldName);
    if (error != null) return error;

    if (value != matchValue) {
      return '$fieldName does not match';
    }

    return null;
  }

  static String? validateCharacterLimit(
    String? value,
    String fieldName,
    int limit, // Character limit
  ) {
    if (value != null && value.length > limit) {
      return '$fieldName must be $limit characters or fewer';
    }
    return null;
  }

  static bool isValidUrl(String url) {
    final urlPattern = r'^(https?:\/\/)?([\w\-]+\.)+[\w\-]+(\/[\w\-]*)*\/?$';
    return RegExp(urlPattern).hasMatch(url);
  }

  // Positive Double (Float) validator
  static String? validatePositiveDouble(String? value, String fieldName) {
    // Allow nullable input (e.g., empty field)
    if (value == null || value.trim().isEmpty) {
      return null; // No error if the input is empty
    }

    // Trim input
    value = value.trim();

    // Check for spaces
    if (value.contains(' ')) {
      return 'No spaces';
    }

    // Check for special characters
    final specialCharacterRegex = RegExp(r'[^\d.]'); // Allows only digits and '.'
    if (specialCharacterRegex.hasMatch(value)) {
      return 'Numbers only';
    }

    // Prevent multiple dots (e.g., "1..2")
    if (value.split('.').length > 2) {
      return 'Invalid decimal';
    }

    // Additional check for positive values
    if (value.startsWith('-')) {
      return 'No negative';
    }

    return null; // Valid input
  }

  static String? validatePositiveInteger(String? value, String fieldName) {

      // Allow nullable input (e.g., empty field)
    if (value == null || value.trim().isEmpty) {
      return null; // No error if the input is empty
    }

    // Trim input
    value = value.trim();

        // Check if the input contains non-digit characters
    if (!RegExp(r'^[0-9]+$').hasMatch(value)) {
      return 'Only positive integer';
    }

    return null;
  }

}
