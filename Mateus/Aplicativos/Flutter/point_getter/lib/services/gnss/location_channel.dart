import 'package:flutter/services.dart';

class LocationChannel {
  static const MethodChannel _channel = MethodChannel('location_channel');

  static Future<void> startLocationService() async {
    try {
      await _channel.invokeMethod('startLocationService');
    } catch (e) {
      print('Erro ao iniciar a coleta de dados Location: $e');
    }
  }

  static Future<void> stopLocationService() async {
    try {
      await _channel.invokeMethod('stopLocationService');
    } catch (e) {
      print('Erro ao parar a coleta de dados Location: $e');
    }
  }
}
