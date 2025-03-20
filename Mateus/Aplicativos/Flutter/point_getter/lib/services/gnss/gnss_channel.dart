/*import 'package:flutter/services.dart';

const MethodChannel _channel = MethodChannel('gnss_data_channel');

class GnssDataChannel {
  static Future<Map<String, dynamic>> collectGnssData() async {
    try {
      final Map<String, dynamic> result =
          await _channel.invokeMethod('collectGnssData');
      return result;
    } on PlatformException catch (e) {
      print("Failed to collect GNSS data: '${e.message}'.");
      return {};
    }
  }
}*/

/*import 'package:flutter/services.dart';

class GnssChannel {
  static const MethodChannel _channel = MethodChannel('gnss_channel');

  // Método para iniciar a coleta de dados GNSS
  static Future<void> startGnssCollection() async {
    try {
      await _channel.invokeMethod('startGnssCollection');
    } catch (e) {
      print('Erro ao iniciar a coleta de dados GNSS: $e');
    }
  }

  // Método para parar a coleta de dados GNSS
  static Future<void> stopGnssCollection() async {
    try {
      await _channel.invokeMethod('stopGnssCollection');
    } catch (e) {
      print('Erro ao parar a coleta de dados GNSS: $e');
    }
  }

  // Método para receber os dados GNSS da parte nativa
  static Stream<Map<String, dynamic>> getGnssDataStream() {
    return EventChannel('gnss_data_stream')
        .receiveBroadcastStream()
        .map((dynamic event) => event as Map<String, dynamic>);
  }
}
*/

import 'package:flutter/services.dart';

class GnssChannel {
  static const MethodChannel _channel = MethodChannel('gnss_channel');

  static Future<void> startGnssCollection() async {
    try {
      await _channel.invokeMethod('startGnssCollection');
    } catch (e) {
      print('Erro ao iniciar a coleta de dados GNSS: $e');
    }
  }

  static Future<void> stopGnssCollection() async {
    try {
      await _channel.invokeMethod('stopGnssCollection');
    } catch (e) {
      print('Erro ao parar a coleta de dados GNSS: $e');
    }
  }
}
