import 'dart:async';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:location/location.dart';
import 'package:point_getter/services/provider/points_provider.dart';
import 'package:point_getter/widgets/maps/maps_widget.dart';
import 'package:provider/provider.dart';

class GnssDataScreen extends StatefulWidget {
  @override
  _GnssDataScreenState createState() => _GnssDataScreenState();
}

class _GnssDataScreenState extends State<GnssDataScreen> {
  List<dynamic>? _locationData;
  List<Map<String, dynamic>> _gnssData = [];
  List<Map<String, dynamic>> _gnssMeasurementData = [];
  late StreamSubscription _locationStreamSubscription;
  late StreamSubscription _gnssStreamSubscription;
  late StreamSubscription _gnssMeasurementStreamSubscription;
  bool _isLoading = true;
  String _errorMessage = '';
  late Location location;

  @override
  void initState() {
    super.initState();
    _startListeningToLocationData();
    _startListeningToGnssData();
    _startListeningToGnssMeasurementData();
    location = Location();
    _checkLocationService();
    _fetchLocationData(); // Obtém a localização assim que a tela é aberta
  }

  @override
  void dispose() {
    _stopListeningToLocationData();
    _stopListeningToGnssData();
    _stopListeningToGnssMeasurementData();
    super.dispose();
  }

  void _fetchLocationData() async {
    try {
      LocationData _currentLocation = await location.getLocation();
      setState(() {
        _locationData = [_currentLocation];
      });
    } catch (e) {
      setState(() {
        _errorMessage = 'Erro ao obter dados de localização: $e';
      });
    }
  }

  void _startListeningToLocationData() {
    const eventChannel = EventChannel('location_data_stream');
    _locationStreamSubscription = eventChannel.receiveBroadcastStream().listen(
      (event) {
        if (event is Map<dynamic, dynamic>) {
          setState(() {
            _locationData = [event];
            _isLoading = false;
            _errorMessage = '';
          });
        } else {
          setState(() {
            _isLoading = false;
            _errorMessage = 'Formato de dados de localização inválido.';
          });
        }
      },
      onError: (error) {
        setState(() {
          _isLoading = false;
          _errorMessage = 'Erro ao obter dados de localização: $error';
        });
      },
    );
  }

  void _stopListeningToLocationData() {
    _locationStreamSubscription.cancel();
  }

  void _startListeningToGnssData() {
    const eventChannel = EventChannel('gnss_data_stream');
    _gnssStreamSubscription = eventChannel.receiveBroadcastStream().listen(
      (event) {
        if (event is List<dynamic>) {
          setState(() {
            _gnssData =
                event.map((item) => Map<String, dynamic>.from(item)).toList();
            _isLoading = false;
            _errorMessage = '';
          });
        } else {
          setState(() {
            _isLoading = false;
            _errorMessage = 'Formato de dados GNSS inválido.';
          });
        }
      },
      onError: (error) {
        setState(() {
          _isLoading = false;
          _errorMessage = 'Erro ao obter dados GNSS: $error';
        });
      },
    );
  }

  void _stopListeningToGnssData() {
    _gnssStreamSubscription.cancel();
  }

  void _startListeningToGnssMeasurementData() {
    const eventChannel = EventChannel('gnss_measurement_data_stream');
    _gnssMeasurementStreamSubscription =
        eventChannel.receiveBroadcastStream().listen(
      (event) {
        /*if (event is List<dynamic>) {
          setState(() {
            _gnssMeasurementData = event.cast<Map<String, dynamic>>();
            _isLoading = false;
            _errorMessage = '';
          });*/
        if (event is List<dynamic>) {
          setState(() {
            _gnssMeasurementData =
                event.map((item) => Map<String, dynamic>.from(item)).toList();
            _isLoading = false;
            _errorMessage = '';
          });
        } else {
          setState(() {
            _isLoading = false;
            _errorMessage = 'Formato de dados GNSS Measurement inválido.';
          });
        }
      },
      onError: (error) {
        setState(() {
          _isLoading = false;
          _errorMessage = 'Erro ao obter dados GNSS Measurement: $error';
        });
      },
    );
  }

  void _stopListeningToGnssMeasurementData() {
    _gnssMeasurementStreamSubscription.cancel();
  }

  void _checkLocationService() async {
    bool _serviceEnabled = await location.serviceEnabled();
    if (!_serviceEnabled) {
      _serviceEnabled = await location.requestService();
      if (!_serviceEnabled) {
        setState(() {
          _isLoading = false;
          _errorMessage = 'Serviço de localização desativado.';
        });
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Dados de Localização e GNSS'),
      ),
      body: Column(
        children: [
          Expanded(
            child: MapsWidget(
              markers: Provider.of<PointsProvider>(context).markers,
              isPaused: false,
            ),
          ),
          Expanded(
            child: Center(
              child: _isLoading
                  ? const CircularProgressIndicator()
                  : _errorMessage.isNotEmpty
                      ? Text(_errorMessage)
                      : SingleChildScrollView(
                          child: Column(
                            mainAxisAlignment: MainAxisAlignment.center,
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              if (_locationData != null &&
                                  _locationData!.isNotEmpty)
                                const Text(
                                  'Dados Location:',
                                  style: TextStyle(
                                    fontWeight: FontWeight.bold,
                                    fontSize: 18,
                                  ),
                                ),
                              ..._locationData![0].entries.map((entry) {
                                return Padding(
                                  padding:
                                      const EdgeInsets.symmetric(vertical: 4.0),
                                  child: Text(
                                    '${entry.key}: ${entry.value}',
                                    style: const TextStyle(
                                      fontSize: 16,
                                      //fontWeight: FontWeight,
                                    ),
                                  ),
                                );
                              }).toList(),
                              const SizedBox(height: 20),
                              if (_gnssData.isNotEmpty)
                                const Text(
                                  'Dados GNSS Measurement:',
                                  style: TextStyle(
                                    fontWeight: FontWeight.bold,
                                    fontSize: 18,
                                  ),
                                ),
                              const SizedBox(height: 10),
                              if (_gnssData.isNotEmpty)
                                Padding(
                                  padding:
                                      const EdgeInsets.symmetric(vertical: 4.0),
                                  child: Column(
                                    crossAxisAlignment:
                                        CrossAxisAlignment.start,
                                    children: [
                                      Text(
                                        'getCurrentTime: ' +
                                            _gnssMeasurementData
                                                .map((data) =>
                                                    '${data['getCurrentTime']}')
                                                .join(', '),
                                        style: const TextStyle(fontSize: 16),
                                      ),
                                      const SizedBox(height: 10),
                                      Text(
                                        'getAccumulatedDeltaRangeMeters: ' +
                                            _gnssMeasurementData
                                                .map((data) =>
                                                    '${data['getAccumulatedDeltaRangeMeters']}')
                                                .join(', '),
                                        style: const TextStyle(fontSize: 16),
                                      ),
                                      const SizedBox(height: 10),
                                      Text(
                                        'getAccumulatedDeltaRangeState: ' +
                                            _gnssMeasurementData
                                                .map((data) =>
                                                    '${data['getAccumulatedDeltaRangeState']}')
                                                .join(', '),
                                        style: const TextStyle(fontSize: 16),
                                      ),
                                      const SizedBox(height: 10),
                                      Text(
                                        'getAccumulatedDeltaRangeUncertaintyMeters: ' +
                                            _gnssMeasurementData
                                                .map((data) =>
                                                    '${data['getAccumulatedDeltaRangeUncertaintyMeters']}')
                                                .join(', '),
                                        style: const TextStyle(fontSize: 16),
                                      ),
                                      const SizedBox(height: 10),
                                      Text(
                                        'getBasebandCn0DbHz: ' +
                                            _gnssMeasurementData
                                                .map((data) =>
                                                    '${data['getBasebandCn0DbHz']}')
                                                .join(', '),
                                        style: const TextStyle(fontSize: 16),
                                      ),
                                      const SizedBox(height: 10),
                                      Text(
                                        'getCarrierFrequencyHz: ' +
                                            _gnssMeasurementData
                                                .map((data) =>
                                                    '${data['getCarrierFrequencyHz']}')
                                                .join(', '),
                                        style: const TextStyle(fontSize: 16),
                                      ),
                                      const SizedBox(height: 10),
                                      Text(
                                        'getCn0DbHz: ' +
                                            _gnssMeasurementData
                                                .map((data) =>
                                                    '${data['getCn0DbHz']}')
                                                .join(', '),
                                        style: const TextStyle(fontSize: 16),
                                      ),
                                      const SizedBox(height: 10),
                                      Text(
                                        'getCodeType: ' +
                                            _gnssMeasurementData
                                                .map((data) =>
                                                    '${data['getCodeType']}')
                                                .join(', '),
                                        style: const TextStyle(fontSize: 16),
                                      ),
                                      const SizedBox(height: 10),
                                      Text(
                                        'getConstellationType: ' +
                                            _gnssMeasurementData
                                                .map((data) =>
                                                    '${data['getConstellationType']}')
                                                .join(', '),
                                        style: const TextStyle(fontSize: 16),
                                      ),
                                      const SizedBox(height: 10),
                                      Text(
                                        'getFullInterSignalBiasNanos: ' +
                                            _gnssMeasurementData
                                                .map((data) =>
                                                    '${data['getFullInterSignalBiasNanos']}')
                                                .join(', '),
                                        style: const TextStyle(fontSize: 16),
                                      ),
                                      const SizedBox(height: 10),
                                      Text(
                                        'getFullInterSignalBiasUncertaintyNanos: ' +
                                            _gnssMeasurementData
                                                .map((data) =>
                                                    '${data['getFullInterSignalBiasUncertaintyNanos']}')
                                                .join(', '),
                                        style: const TextStyle(fontSize: 16),
                                      ),
                                      const SizedBox(height: 10),
                                      Text(
                                        'getMultipathIndicator: ' +
                                            _gnssMeasurementData
                                                .map((data) =>
                                                    '${data['getMultipathIndicator']}')
                                                .join(', '),
                                        style: const TextStyle(fontSize: 16),
                                      ),
                                      const SizedBox(height: 10),
                                      Text(
                                        'getPseudorangeRateMetersPerSecond: ' +
                                            _gnssMeasurementData
                                                .map((data) =>
                                                    '${data['getPseudorangeRateMetersPerSecond']}')
                                                .join(', '),
                                        style: const TextStyle(fontSize: 16),
                                      ),
                                      const SizedBox(height: 10),
                                      Text(
                                        'getPseudorangeRateUncertaintyMetersPerSecond: ' +
                                            _gnssMeasurementData
                                                .map((data) =>
                                                    '${data['getPseudorangeRateUncertaintyMetersPerSecond']}')
                                                .join(', '),
                                        style: const TextStyle(fontSize: 16),
                                      ),
                                      const SizedBox(height: 10),
                                      Text(
                                        'getReceivedSvTimeNanos: ' +
                                            _gnssMeasurementData
                                                .map((data) =>
                                                    '${data['getReceivedSvTimeNanos']}')
                                                .join(', '),
                                        style: const TextStyle(fontSize: 16),
                                      ),
                                      const SizedBox(height: 10),
                                      Text(
                                        'getReceivedSvTimeUncertaintyNanos: ' +
                                            _gnssMeasurementData
                                                .map((data) =>
                                                    '${data['getReceivedSvTimeUncertaintyNanos']}')
                                                .join(', '),
                                        style: const TextStyle(fontSize: 16),
                                      ),
                                      const SizedBox(height: 10),
                                      Text(
                                        'getConstellationType: ' +
                                            _gnssMeasurementData
                                                .map((data) =>
                                                    '${data['getConstellationType']}')
                                                .join(', '),
                                        style: const TextStyle(fontSize: 16),
                                      ),
                                      const SizedBox(height: 10),
                                      Text(
                                        'getSatelliteInterSignalBiasNanos: ' +
                                            _gnssMeasurementData
                                                .map((data) =>
                                                    '${data['getSatelliteInterSignalBiasNanos']}')
                                                .join(', '),
                                        style: const TextStyle(fontSize: 16),
                                      ),
                                      const SizedBox(height: 10),
                                      Text(
                                        'getSatelliteInterSignalBiasUncertaintyNanos: ' +
                                            _gnssMeasurementData
                                                .map((data) =>
                                                    '${data['getSatelliteInterSignalBiasUncertaintyNanos']}')
                                                .join(', '),
                                        style: const TextStyle(fontSize: 16),
                                      ),
                                      const SizedBox(height: 10),
                                      Text(
                                        'getSnrInDb: ' +
                                            _gnssMeasurementData
                                                .map((data) =>
                                                    '${data['getSnrInDb']}')
                                                .join(', '),
                                        style: const TextStyle(fontSize: 16),
                                      ),
                                      const SizedBox(height: 10),
                                      Text(
                                        'getState: ' +
                                            _gnssMeasurementData
                                                .map((data) =>
                                                    '${data['getState']}')
                                                .join(', '),
                                        style: const TextStyle(fontSize: 16),
                                      ),
                                      const SizedBox(height: 10),
                                      Text(
                                        'getConstellationType: ' +
                                            _gnssMeasurementData
                                                .map((data) =>
                                                    '${data['getConstellationType']}')
                                                .join(', '),
                                        style: const TextStyle(fontSize: 16),
                                      ),
                                      const SizedBox(height: 10),
                                      Text(
                                        'getSvid: ' +
                                            _gnssMeasurementData
                                                .map((data) =>
                                                    '${data['getSvid']}')
                                                .join(', '),
                                        style: const TextStyle(fontSize: 16),
                                      ),
                                      const SizedBox(height: 10),
                                      Text(
                                        'getTimeOffsetNanos: ' +
                                            _gnssMeasurementData
                                                .map((data) =>
                                                    '${data['getTimeOffsetNanos']}')
                                                .join(', '),
                                        style: const TextStyle(fontSize: 16),
                                      ),
                                      const SizedBox(height: 10),
                                      Text(
                                        'hasBasebandCn0DbHz: ' +
                                            _gnssMeasurementData
                                                .map((data) =>
                                                    '${data['hasBasebandCn0DbHz']}')
                                                .join(', '),
                                        style: const TextStyle(fontSize: 16),
                                      ),
                                      const SizedBox(height: 10),
                                      Text(
                                        'hasCarrierFrequencyHz: ' +
                                            _gnssMeasurementData
                                                .map((data) =>
                                                    '${data['hasCarrierFrequencyHz']}')
                                                .join(', '),
                                        style: const TextStyle(fontSize: 16),
                                      ),
                                      const SizedBox(height: 10),
                                      Text(
                                        'hasCodeType: ' +
                                            _gnssMeasurementData
                                                .map((data) =>
                                                    '${data['hasCodeType']}')
                                                .join(', '),
                                        style: const TextStyle(fontSize: 16),
                                      ),
                                      const SizedBox(height: 10),
                                      Text(
                                        'hasFullInterSignalBiasNanos: ' +
                                            _gnssMeasurementData
                                                .map((data) =>
                                                    '${data['hasFullInterSignalBiasNanos']}')
                                                .join(', '),
                                        style: const TextStyle(fontSize: 16),
                                      ),
                                      const SizedBox(height: 10),
                                      Text(
                                        'hasFullInterSignalBiasUncertaintyNanos: ' +
                                            _gnssMeasurementData
                                                .map((data) =>
                                                    '${data['hasFullInterSignalBiasUncertaintyNanos']}')
                                                .join(', '),
                                        style: const TextStyle(fontSize: 16),
                                      ),
                                      const SizedBox(height: 10),
                                      Text(
                                        'hasSatelliteInterSignalBiasNanos: ' +
                                            _gnssMeasurementData
                                                .map((data) =>
                                                    '${data['hasSatelliteInterSignalBiasNanos']}')
                                                .join(', '),
                                        style: const TextStyle(fontSize: 16),
                                      ),
                                      const SizedBox(height: 10),
                                      Text(
                                        'hasSatelliteInterSignalBiasUncertaintyNanos: ' +
                                            _gnssMeasurementData
                                                .map((data) =>
                                                    '${data['hasSatelliteInterSignalBiasUncertaintyNanos']}')
                                                .join(', '),
                                        style: const TextStyle(fontSize: 16),
                                      ),
                                      const SizedBox(height: 10),
                                      Text(
                                        'hasSnrInDb: ' +
                                            _gnssMeasurementData
                                                .map((data) =>
                                                    '${data['hasSnrInDb']}')
                                                .join(', '),
                                        style: const TextStyle(fontSize: 16),
                                      ),
                                    ],
                                  ),
                                ),
                              const SizedBox(height: 20),
                              if (_gnssData.isNotEmpty)
                                const Text(
                                  'Dados GNSS:',
                                  style: TextStyle(
                                    fontWeight: FontWeight.bold,
                                    fontSize: 18,
                                  ),
                                ),
                              const SizedBox(height: 10),
                              if (_gnssData.isNotEmpty)
                                Padding(
                                  padding:
                                      const EdgeInsets.symmetric(vertical: 4.0),
                                  child: Column(
                                    crossAxisAlignment:
                                        CrossAxisAlignment.start,
                                    children: [
                                      Text(
                                        'getCurrentTime: ' +
                                            _gnssData
                                                .map((data) =>
                                                    '${data['getCurrentTime']}')
                                                .join(', '),
                                        style: const TextStyle(fontSize: 16),
                                      ),
                                      const SizedBox(height: 10),
                                      Text(
                                        'svid: ' +
                                            _gnssData
                                                .map(
                                                    (data) => '${data['svid']}')
                                                .join(', '),
                                        style: const TextStyle(fontSize: 16),
                                      ),
                                      const SizedBox(height: 10),
                                      Text(
                                        'constellationType: ' +
                                            _gnssData
                                                .map((data) =>
                                                    '${data['constellationType']}')
                                                .join(', '),
                                        style: const TextStyle(fontSize: 16),
                                      ),
                                      const SizedBox(height: 10),
                                      Text(
                                        'cn0DbHz: ' +
                                            _gnssData
                                                .map((data) =>
                                                    '${data['cn0DbHz']}')
                                                .join(', '),
                                        style: const TextStyle(fontSize: 16),
                                      ),
                                      const SizedBox(height: 10),
                                      Text(
                                        'azimuthDegrees: ' +
                                            _gnssData
                                                .map((data) =>
                                                    '${data['azimuthDegrees']}')
                                                .join(', '),
                                        style: const TextStyle(fontSize: 16),
                                      ),
                                      const SizedBox(height: 10),
                                      Text(
                                        'elevationDegrees: ' +
                                            _gnssData
                                                .map((data) =>
                                                    '${data['elevationDegrees']}')
                                                .join(', '),
                                        style: const TextStyle(fontSize: 16),
                                      ),
                                      const SizedBox(height: 10),
                                      Text(
                                        'hasAlmanacData: ' +
                                            _gnssData
                                                .map((data) =>
                                                    '${data['hasAlmanacData']}')
                                                .join(', '),
                                        style: const TextStyle(fontSize: 16),
                                      ),
                                      const SizedBox(height: 10),
                                      Text(
                                        'hasEphemerisData: ' +
                                            _gnssData
                                                .map((data) =>
                                                    '${data['hasEphemerisData']}')
                                                .join(', '),
                                        style: const TextStyle(fontSize: 16),
                                      ),
                                      const SizedBox(height: 10),
                                      Text(
                                        'usedInFix: ' +
                                            _gnssData
                                                .map((data) =>
                                                    '${data['usedInFix']}')
                                                .join(', '),
                                        style: const TextStyle(fontSize: 16),
                                      ),
                                    ],
                                  ),
                                ),
                              const SizedBox(height: 10),
                            ],
                          ),
                        ),
            ),
          ),
        ],
      ),
    );
  }
}
