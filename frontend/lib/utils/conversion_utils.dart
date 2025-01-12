class ConversionUtils {
  // Convert height from cm to feet (rounded down)
  static double cmToFeet(double cm) {
    return cm * 0.0328084; // Conversion factor
  }

  // Convert height from feet to cm (rounded down)
  static double feetToCm(double feet) {
    return (feet * 30.48).ceilToDouble(); // Conversion factor with rounding up
  }

  // Convert weight from kg to lbs
  static double kgToLbs(double kg) {
    return kg * 2.20462; // Conversion factor
  }

  // Convert weight from lbs to kg
  static double lbsToKg(double lbs) {
    return lbs / 2.20462; // Conversion factor
  }
}
