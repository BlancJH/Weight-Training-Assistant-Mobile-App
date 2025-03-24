import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:frontend_1/utils/design_utils.dart';

class AlertWidget {
  /// Shows a customisable Cupertino-style alert dialog.
  static Future<void> show({
    required BuildContext context,
    required String title,
    required String content,
    String cancelText = "Cancel",
    String confirmText = "OK",
    VoidCallback? onConfirm,
  }) {
    return showCupertinoDialog(
      context: context,
      builder: (BuildContext context) {
        return CupertinoTheme(
          data: CupertinoThemeData(
            brightness: Brightness.dark,
            // Optionally customize further (e.g., primaryColor, textTheme) if needed.
          ),
          child: CupertinoAlertDialog(
            title: Text(title),
            content: Text(content),
            actions: <Widget>[
              CupertinoDialogAction(
                child: Text(cancelText),
                onPressed: () => Navigator.of(context).pop(),
              ),
              CupertinoDialogAction(
                child: Text(confirmText),
                onPressed: () {
                  Navigator.of(context).pop();
                  if (onConfirm != null) onConfirm();
                },
              ),
            ],
          ),
        );
      },
    );
  }
}
