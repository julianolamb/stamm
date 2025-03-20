import 'package:flutter/services.dart';

class GnssChannel {
  static const MethodChannel _channel =
      MethodChannel('gnss_measurement_channel');

  static Future<void> startGnssMeasurementService() async {
    try {
      await _channel.invokeMethod('startGnssMeasurementService');
    } catch (e) {
      print('Erro ao iniciar a coleta de dados GNSS Measurement: $e');
    }
  }

  static Future<void> stopGnssMeasurementService() async {
    try {
      await _channel.invokeMethod('stopGnssMeasurementService');
    } catch (e) {
      print('Erro ao parar a coleta de dados GNSS Measurement: $e');
    }
  }
}
