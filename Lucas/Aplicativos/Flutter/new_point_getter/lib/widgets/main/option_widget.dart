import 'package:flutter/material.dart';

class OptionWidget extends StatelessWidget {
  final String title;
  final Color color;
  final String iconImagePath;
  final VoidCallback onTap;

  const OptionWidget({
    required this.title,
    required this.color,
    required this.iconImagePath,
    this.onTap = _defaultOnTap,
  });

  static void _defaultOnTap() {}

  @override
  Widget build(BuildContext context) {
    return ElevatedButton(
      onPressed: onTap,
      style: ElevatedButton.styleFrom(
        backgroundColor: color,
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(10),
        ),
        elevation: 5,
        padding: const EdgeInsets.all(0),
      ),
      child: Container(
        width: 140,
        height: 160,
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Text(
              title,
              style: const TextStyle(
                color: Colors.black,
                fontWeight: FontWeight.bold,
                fontSize: 24,
              ),
              textAlign: TextAlign.center,
            ),
            const SizedBox(height: 10),
            Image.asset(
              iconImagePath,
              width: 60,
              height: 60,
            ),
          ],
        ),
      ),
    );
  }
}
