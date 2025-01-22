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
    final passwordRegex = RegExp(
      r'^(?=.*[A-Z])(?=.*[a-z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$',
    );

    return _validate(
      value,
      'Password',
      minLength: 8, // Enforces minimum length
      customCondition: (val) => passwordRegex.hasMatch(val),
      customErrorMessage:
          'Password must include at least:\nOne uppercase letter\nOne lowercase letter\nOne number\nOne special character',
    );
  } //TODO: add error message widget to show invalid option for the password live time.

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
      return '$fieldName must not contain spaces';
    }

    // Check for special characters
    final specialCharacterRegex = RegExp(r'[^\d.]'); // Allows only digits and '.'
    if (specialCharacterRegex.hasMatch(value)) {
      return '$fieldName must not contain special characters (e.g., "-")';
    }

    // Prevent multiple dots (e.g., "1..2")
    if (value.split('.').length > 2) {
      return '$fieldName must be a valid decimal number';
    }

    // Additional check for positive values (do not parse to double yet)
    if (value.startsWith('-')) {
      return '$fieldName must not be negative';
    }

    return null; // Valid input
  }

}
