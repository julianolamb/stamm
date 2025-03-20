import 'dart:async';
import 'dart:math' show atan2, cos, pi, pow, sin, sqrt;

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:geolocator/geolocator.dart';
import 'package:location/location.dart' hide LocationAccuracy;
import 'package:point_getter/models/point_model.dart';
import 'package:point_getter/models/location_model.dart';
import 'package:point_getter/models/gnss_status_model.dart';
import 'package:point_getter/models/gnss_measurement_model.dart';
import 'package:point_getter/services/provider/database_provider.dart';

class CollectInProgressWidget extends StatefulWidget {
  final List<Point> points;
  final List<Map<String, dynamic>> collectTimes;
  final int pauseInterval;
  final bool isPauseInMinutes;
  final Function(bool) onPauseChanged;
  final VoidCallback onBack;

  CollectInProgressWidget({
    required this.points,
    required this.collectTimes,
    required this.pauseInterval,
    required this.isPauseInMinutes,
    required this.onPauseChanged,
    required this.onBack,
  });

  @override
  _CollectInProgressWidgetState createState() =>
      _CollectInProgressWidgetState();
}

class _CollectInProgressWidgetState extends State<CollectInProgressWidget> {
  int _currentPointIndex = 0;
  int _currentRoundIndex = 0;
  int _currentRoundTime = 0;
  Timer? _timer;
  bool _showButtons = true;
  bool _isPaused = false;
  //late StreamSubscription _locationStreamSubscription;
  late StreamSubscription _gnssStreamSubscription;
  late StreamSubscription _gnssMeasurementStreamSubscription;
  late StreamSubscription<Position>? _positionSubscription;
  Location _location = Location();

  @override
  void dispose() {
    _timer?.cancel();
    _stopListeningToLocationData();
    _stopListeningToGnssData();
    _stopListeningToGnssMeasurementData();
    widget.onPauseChanged(false);
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    String title = _isPaused ? 'PAUSA' : 'Ponto ${_currentPointIndex + 1}';

    return Container(
      color: Colors.white,
      child: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Text(
              title,
              style: TextStyle(
                fontSize: 24,
                fontWeight: FontWeight.bold,
              ),
            ),
            SizedBox(height: 20),
            _showButtons
                ? Row(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      ElevatedButton(
                        onPressed: () {
                          _startCollect();
                        },
                        child: Text('Iniciar'),
                      ),
                      SizedBox(width: 20),
                      ElevatedButton(
                        onPressed: () {
                          widget.onBack();
                        },
                        child: Text('Voltar'),
                      ),
                    ],
                  )
                : Column(
                    children: [
                      Text(
                        'Ponto ${_currentPointIndex + 1}',
                        style: TextStyle(fontSize: 20),
                      ),
                      SizedBox(height: 10),
                      Text(
                        'Rodada - ${_currentRoundIndex + 1}',
                        style: TextStyle(fontSize: 20),
                      ),
                      SizedBox(height: 10),
                      Text(
                        _currentRoundTime > 0
                            ? 'Tempo - $_currentRoundTime segundos'
                            : '',
                        style: TextStyle(fontSize: 20),
                      ),
                    ],
                  ),
          ],
        ),
      ),
    );
  }

  void _startCollect() async {
    LocationData currentPosition = await _determinePosition();

    Point currentPoint = widget.points[_currentPointIndex];
    double distanceInMeters = _calculateDistance(
      currentPosition.latitude!,
      currentPosition.longitude!,
      currentPoint.latitude,
      currentPoint.longitude,
    );

    if (distanceInMeters < 10000) {
      setState(() {
        _showButtons = false;
      });
      // Iniciar o recebimento de dados junto com o timer
      _startCollectTimer();
    } else {
      _showAlert('Aproxime-se do ponto ${_currentPointIndex + 1}');
    }
  }

  Future<LocationData> _determinePosition() async {
    Location location = Location();
    return await location.getLocation();
  }

  double _calculateDistance(
    double startLatitude,
    double startLongitude,
    double endLatitude,
    double endLongitude,
  ) {
    const int earthRadius = 6371000; // Raio médio da Terra em metros

    // Conversão de graus para radianos
    double lat1Rad = startLatitude * (pi / 180);
    double lon1Rad = startLongitude * (pi / 180);
    double lat2Rad = endLatitude * (pi / 180);
    double lon2Rad = endLongitude * (pi / 180);

    // Diferença das latitudes e longitudes
    double latDiff = lat2Rad - lat1Rad;
    double lonDiff = lon2Rad - lon1Rad;

    // Fórmula de Haversine
    double a = pow(sin(latDiff / 2), 2) +
        cos(lat1Rad) * cos(lat2Rad) * pow(sin(lonDiff / 2), 2);
    double c = 2 * atan2(sqrt(a), sqrt(1 - a));

    // Distância em metros
    return earthRadius * c;
  }

  void _startCollectTimer() {
    widget.onPauseChanged(false);
    _startListeningToLocationData();
    _startListeningToGnssData();
    _startListeningToGnssMeasurementData();
    List<Map<String, dynamic>> collectTimes = widget.collectTimes;

    if (_currentRoundIndex < collectTimes.length) {
      int timeInSeconds = collectTimes[_currentRoundIndex]['isMinutes']
          ? collectTimes[_currentRoundIndex]['time'] * 60
          : collectTimes[_currentRoundIndex]['time'];

      setState(() {
        _currentRoundTime = timeInSeconds;
      });

      _timer = Timer.periodic(Duration(seconds: 1), (timer) {
        setState(() {
          _currentRoundTime--;
          if (_currentRoundTime <= 0) {
            _timer?.cancel();
            if (_isPaused) {
              _isPaused = false;
              _currentRoundIndex++;
              _startCollectTimer();
            } else {
              _stopListeningToLocationData();
              _stopListeningToGnssData();
              _stopListeningToGnssMeasurementData();
              _isPaused = true;
              widget.onPauseChanged(true);
              _startPauseTimer();
            }
          }
        });
      });
    } else {
      _advanceToPoint();
    }
  }

  void _startPauseTimer() {
    int pauseIntervalInSeconds = widget.isPauseInMinutes
        ? widget.pauseInterval * 60
        : widget.pauseInterval;

    _currentRoundTime = pauseIntervalInSeconds;
    _timer = Timer.periodic(Duration(seconds: 1), (timer) {
      setState(() {
        _currentRoundTime--;
        if (_currentRoundTime <= 0) {
          _timer?.cancel();
          _currentRoundIndex++;
          _isPaused = false;
          if (_currentRoundIndex < widget.collectTimes.length) {
            _showButtons = false; // Esconde os botões de controle
            _startCollectTimer(); // Inicia automaticamente a próxima rodada
          } else {
            /*_stopListeningToLocationData();
            _stopListeningToGnssData();
            _stopListeningToGnssMeasurementData();*/
            _advanceToPoint();
          }
        }
      });
    });
  }

  void _advanceToPoint() {
    if (_currentPointIndex < widget.points.length - 1) {
      setState(() {
        _currentPointIndex++;
        _currentRoundIndex = 0;
        _currentRoundTime = 0;
        _showButtons = true;
      });
    } else {
      _showAlert('Coleta concluída com sucesso');
    }
  }

  void _showAlert(String message) {
    showDialog(
      context: context,
      builder: (BuildContext context) {
        return AlertDialog(
          title: Text('Atenção'),
          content: Text(message),
          actions: [
            TextButton(
              onPressed: () {
                Navigator.of(context).pop();
                if (message == 'Coleta concluída com sucesso') {
                  Navigator.of(context).popUntil((route) => route.isFirst);
                }
              },
              child: Text('OK'),
            ),
          ],
        );
      },
    );
  }

  LocationDataModel _mapPositionToLocationData(Position position) {
    return LocationDataModel(
      pontoID: _currentPointIndex + 1,
      rodadaID: _currentRoundIndex + 1,
      getCurrentTime: DateTime.now().toString(),
      timestamp: position.timestamp?.millisecondsSinceEpoch.toDouble() ?? 0.0,
      latitude: position.latitude,
      longitude: position.longitude,
      accuracy: position.accuracy ?? 0.0,
      altitude: position.altitude ?? 0.0,
      altitudeAccuracy: position.altitudeAccuracy ?? 0.0,
      floor: position.floor,
      speed: position.speed ?? 0.0,
      speedAccuracy: position.speedAccuracy ?? 0.0,
      heading: position.heading ?? 0.0,
      headingAccuracy: position.headingAccuracy ?? 0.0,
      isMock: position.isMocked ? 1 : 0,
    );
  }

  void _startListeningToLocationData() async {
    bool serviceEnabled = await Geolocator.isLocationServiceEnabled();
    if (!serviceEnabled) {
      print('Location services are disabled.');
      return;
    }

    LocationPermission permission = await Geolocator.checkPermission();
    if (permission == LocationPermission.denied) {
      permission = await Geolocator.requestPermission();
      if (permission != LocationPermission.whileInUse &&
          permission != LocationPermission.always) {
        print('Location permissions are denied.');
        return;
      }
    }

    _positionSubscription = Geolocator.getPositionStream(
        locationSettings: AndroidSettings(
      forceLocationManager: true,
      accuracy: LocationAccuracy.best,
      distanceFilter: 0,
      intervalDuration: Duration(seconds: 1),
    )).listen((Position position) {
      DatabaseProvider.transaction((txn) async {
        await txn.insert(
            'tb_location', _mapPositionToLocationData(position).toMap());
      });
    }, onError: (error) {
      print('Error obtaining location data: $error');
    });
  }

  void _stopListeningToLocationData() {
    _positionSubscription?.cancel();
    _positionSubscription = null;
  }

  /*LocationDataModel _mapLocationData(Map<dynamic, dynamic> eventData) {
    return LocationDataModel(
      pontoID: _currentPointIndex + 1,
      rodadaID: _currentRoundIndex + 1,
      getCurrentTime: eventData['getCurrentTime'],
      getAccuracy: eventData['getAccuracy'],
      getAltitude: eventData['getAltitude'],
      getBearing: eventData['getBearing'],
      getBearingAccuracyDegrees: eventData['getBearingAccuracyDegrees'],
      getElapsedRealtimeAgeMillis: eventData['getElapsedRealtimeAgeMillis'],
      getElapsedRealtimeMillis: eventData['getElapsedRealtimeMillis'],
      getElapsedRealtimeNanos: eventData['getElapsedRealtimeNanos'],
      getElapsedRealtimeUncertaintyNanos:
          eventData['getElapsedRealtimeUncertaintyNanos'],
      getExtras: eventData['getExtras'],
      getLatitude: eventData['getLatitude'],
      getLongitude: eventData['getLongitude'],
      getProvider: eventData['getProvider'],
      getSpeed: eventData['getSpeed'],
      getSpeedAccuracyMetersPerSecond:
          eventData['getSpeedAccuracyMetersPerSecond'],
      getTime: eventData['getTime'],
      getVerticalAccuracyMeters: eventData['getVerticalAccuracyMeters'],
      hasAccuracy: eventData['hasAccuracy'],
      hasAltitude: eventData['hasAltitude'],
      hasBearing: eventData['hasBearing'],
      hasBearingAccuracy: eventData['hasBearingAccuracy'],
      hasElapsedRealtimeUncertaintyNanos:
          eventData['hasElapsedRealtimeUncertaintyNanos'],
      hasSpeed: eventData['hasSpeed'],
      hasSpeedAccuracy: eventData['hasSpeedAccuracy'],
      hasVerticalAccuracy: eventData['hasVerticalAccuracy'],
      isComplete: eventData['isComplete'],
      isMock: eventData['isMock'],
    );
  }*/

  /*void _startListeningToLocationData() {
    const eventChannel = EventChannel('location_data_stream');
    _locationStreamSubscription = eventChannel.receiveBroadcastStream().listen(
      (event) {
        if (event is Map<dynamic, dynamic>) {
          // Usar uma transação para inserir os dados de localização
          DatabaseProvider.transaction((txn) async {
            await txn.insert(
              'tb_location',
              _mapLocationData(event)
                  .toMap(), // Converte o objeto para um mapa para inserção
            );
          });
        }
      },
      onError: (error) {
        print('Erro ao obter dados de localização: $error');
      },
    );
  }

  void _stopListeningToLocationData() {
    _locationStreamSubscription.cancel();
  }*/

  GnssStatusModel _mapGnssStatusData(Map<dynamic, dynamic> eventData) {
    return GnssStatusModel(
      pontoID: _currentPointIndex + 1,
      rodadaID: _currentRoundIndex + 1,
      getCurrentTime: eventData['getCurrentTime'],
      getBasebandgetCn0DbHz: eventData['getBasebandCn0DbHz'],
      getCarrierFrequencyHz: eventData['getCarrierFrequencyHz'],
      getCn0DbHz: eventData['getCn0DbHz'],
      getSatelliteCount: eventData['getSatelliteCount'],
      getSvid: eventData['getSvid'],
      getConstellationType: eventData['getConstellationType'],
      getAzimuthDegrees: eventData['getAzimuthDegrees'],
      getElevationDegrees: eventData['getElevationDegrees'],
      hasAlmanacData: eventData['hasAlmanacData'],
      hasEphemerisData: eventData['hasEphemerisData'],
      hasBasebandCn0DbHz: eventData['hasBasebandCn0DbHz'],
      hasCarrierFrequencyHz: eventData['hasCarrierFrequencyHz'],
      usedInFix: eventData['usedInFix'],
    );
  }

  void _startListeningToGnssData() {
    const eventChannel = EventChannel('gnss_data_stream');
    _gnssStreamSubscription = eventChannel.receiveBroadcastStream().listen(
      (event) async {
        if (event is List<dynamic>) {
          // Iniciar uma transação
          final db = await DatabaseProvider.database;
          await db.transaction((txn) async {
            try {
              // Iterar sobre os itens recebidos
              for (var item in event) {
                if (item is Map<dynamic, dynamic>) {
                  // Mapear os dados recebidos para o modelo de dados de GNSS Status
                  var gnssData = _mapGnssStatusData(item);
                  // Inserir os dados na tabela usando a transação
                  await txn.insert('tb_gnss_status', gnssData.toMap());
                }
              }
            } catch (error) {
              // Lidar com erros durante a transação
              print(
                  'Erro durante a transação de inserção de dados de GNSS Status: $error');
            }
          });
        }
      },
      onError: (error) {
        // Lógica para lidar com erros ao receber os dados de GNSS Status
        print('Erro ao obter dados de GNSS Status: $error');
      },
    );
  }

  void _stopListeningToGnssData() {
    _gnssStreamSubscription.cancel();
  }

  GnssMeasurementModel _mapGnssMeasurementData(
      Map<dynamic, dynamic> eventData) {
    return GnssMeasurementModel(
      pontoID: _currentPointIndex + 1,
      rodadaID: _currentRoundIndex + 1,
      getCurrentTime: eventData['getCurrentTime'],
      getAccumulatedDeltaRangeMeters:
          eventData['getAccumulatedDeltaRangeMeters'],
      getAccumulatedDeltaRangeState: eventData['getAccumulatedDeltaRangeState'],
      getAccumulatedDeltaRangeUncertaintyMeters:
          eventData['getAccumulatedDeltaRangeUncertaintyMeters'],
      getBasebandCn0DbHz: eventData['getBasebandCn0DbHz'],
      getCarrierFrequencyHz: eventData['getCarrierFrequencyHz'],
      getCn0DbHz: eventData['getCn0DbHz'],
      getCodeType: eventData['getCodeType'],
      getConstellationType: eventData['getConstellationType'],
      getFullInterSignalBiasNanos: eventData['getFullInterSignalBiasNanos'],
      getFullInterSignalBiasUncertaintyNanos:
          eventData['getFullInterSignalBiasUncertaintyNanos'],
      getMultipathIndicator: eventData['getMultipathIndicator'],
      getPseudorangeRateMetersPerSecond:
          eventData['getPseudorangeRateMetersPerSecond'],
      getPseudorangeRateUncertaintyMetersPerSecond:
          eventData['getPseudorangeRateUncertaintyMetersPerSecond'],
      getReceivedSvTimeNanos: eventData['getReceivedSvTimeNanos'],
      getReceivedSvTimeUncertaintyNanos:
          eventData['getReceivedSvTimeUncertaintyNanos'],
      getSatelliteInterSignalBiasNanos:
          eventData['getSatelliteInterSignalBiasNanos'],
      getSatelliteInterSignalBiasUncertaintyNanos:
          eventData['getSatelliteInterSignalBiasUncertaintyNanos'],
      getSnrInDb: eventData['getSnrInDb'],
      getState: eventData['getState'],
      getSvid: eventData['getSvid'],
      getTimeOffsetNanos: eventData['getTimeOffsetNanos'],
      hasBasebandCn0DbHz: eventData['hasBasebandCn0DbHz'],
      hasCarrierFrequencyHz: eventData['hasCarrierFrequencyHz'],
      hasCodeType: eventData['hasCodeType'],
      hasFullInterSignalBiasNanos: eventData['hasFullInterSignalBiasNanos'],
      hasFullInterSignalBiasUncertaintyNanos:
          eventData['hasFullInterSignalBiasUncertaintyNanos'],
      hasSatelliteInterSignalBiasNanos:
          eventData['hasSatelliteInterSignalBiasNanos'],
      hasSatelliteInterSignalBiasUncertaintyNanos:
          eventData['hasSatelliteInterSignalBiasUncertaintyNanos'],
      hasSnrInDb: eventData['hasSnrInDb'],
    );
  }

  void _startListeningToGnssMeasurementData() {
    const eventChannel = EventChannel('gnss_measurement_data_stream');
    _gnssMeasurementStreamSubscription =
        eventChannel.receiveBroadcastStream().listen(
      (event) async {
        if (event is List<dynamic>) {
          // Iniciar uma transação
          final db = await DatabaseProvider.database;
          await db.transaction((txn) async {
            try {
              // Iterar sobre os itens recebidos
              for (var item in event) {
                if (item is Map<dynamic, dynamic>) {
                  // Mapear os dados recebidos para o modelo de dados de GNSS Measurements
                  var gnssMeasurementData = _mapGnssMeasurementData(item);
                  // Inserir os dados na tabela usando a transação
                  await txn.insert(
                      'tb_gnss_measurement', gnssMeasurementData.toMap());
                }
              }
            } catch (error) {
              // Lidar com erros durante a transação
              print(
                  'Erro durante a transação de inserção de dados de GNSS Measurements: $error');
            }
          });
        }
      },
      onError: (error) {
        // Lógica para lidar com erros ao receber os dados de GNSS Measurements
        print('Erro ao obter dados de GNSS Measurements: $error');
      },
    );
  }

  void _stopListeningToGnssMeasurementData() {
    _gnssMeasurementStreamSubscription.cancel();
  }
}
