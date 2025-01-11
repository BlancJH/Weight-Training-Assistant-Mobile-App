class ConversionUtils {
  // Convert height from cm to feet
  static double cmToFeet(double cm) {
    return cm * 0.0328084; // Conversion factor
  }

  // Convert height from feet to cm
  static double feetToCm(double feet) {
    return feet * 30.48; // Conversion factor
  }
}
