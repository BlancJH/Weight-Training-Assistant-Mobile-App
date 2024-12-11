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
    return _validate(
      value,
      'Password',
      minLength: 6,
    );
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
}
