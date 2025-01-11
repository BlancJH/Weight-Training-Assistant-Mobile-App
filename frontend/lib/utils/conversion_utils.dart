class ConversionUtils {
  // Convert height from cm to feet (rounded down)
  static double cmToFeet(double cm) {
    return cm * 0.0328084; // Conversion factor
  }

  // Convert height from feet to cm (rounded down)
  static double feetToCm(double feet) {
    return (feet * 30.48).ceilToDouble(); // Conversion factor with rounding up
  }
}
