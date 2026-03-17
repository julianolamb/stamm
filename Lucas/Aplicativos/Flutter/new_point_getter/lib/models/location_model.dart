class LocationDataModel {
  final int pontoID;
  final int rodadaID;
  final String getCurrentTime;
  final double accuracy;
  final double altitude;
  final double altitudeAccuracy;
  final double heading;
  final double headingAccuracy;
  final double speed;
  final double speedAccuracy;
  final int? floor;
  final int isMock;
  final double latitude;
  final double longitude;
  final double timestamp;

  LocationDataModel({
    required this.pontoID,
    required this.rodadaID,
    required this.getCurrentTime,
    required this.accuracy,
    required this.altitude,
    required this.altitudeAccuracy,
    required this.heading,
    required this.headingAccuracy,
    required this.speed,
    required this.speedAccuracy,
    required this.floor,
    required this.isMock,
    required this.latitude,
    required this.longitude,
    required this.timestamp,
  });

  Map<String, dynamic> toMap() {
    return {
      'ponto_id': pontoID,
      'rodada_id': rodadaID,
      'get_current_time': getCurrentTime,
      'accuracy': accuracy,
      'altitude': altitude,
      'altitude_accuracy': altitudeAccuracy,
      'heading': heading,
      'heading_accuracy': headingAccuracy,
      'speed': speed,
      'speed_accuracy': speedAccuracy,
      'floor': floor ?? 0,
      'is_mock': isMock,
      'latitude': latitude,
      'longitude': longitude,
      'timestamp': timestamp,
    };
  }
}


/*class LocationDataModel {
  final int pontoID;
  final int rodadaID;
  final String getCurrentTime;
  final double getAccuracy;
  final double getAltitude;
  final double getBearing;
  final double getBearingAccuracyDegrees;
  final int getElapsedRealtimeAgeMillis;
  final int getElapsedRealtimeMillis;
  final int getElapsedRealtimeNanos;
  final double getElapsedRealtimeUncertaintyNanos;
  final String getExtras;
  final double getLatitude;
  final double getLongitude;
  final String getProvider;
  final double getSpeed;
  final double getSpeedAccuracyMetersPerSecond;
  final int getTime;
  final double getVerticalAccuracyMeters;
  final bool hasAccuracy;
  final bool hasAltitude;
  final bool hasBearing;
  final bool hasBearingAccuracy;
  final bool hasElapsedRealtimeUncertaintyNanos;
  final bool hasSpeed;
  final bool hasSpeedAccuracy;
  final bool hasVerticalAccuracy;
  final bool isComplete;
  final bool isMock;

  LocationDataModel({
    required this.pontoID,
    required this.rodadaID,
    required this.getCurrentTime,
    required this.getAccuracy,
    required this.getAltitude,
    required this.getBearing,
    required this.getBearingAccuracyDegrees,
    required this.getElapsedRealtimeAgeMillis,
    required this.getElapsedRealtimeMillis,
    required this.getElapsedRealtimeNanos,
    required this.getElapsedRealtimeUncertaintyNanos,
    required this.getExtras,
    required this.getLatitude,
    required this.getLongitude,
    required this.getProvider,
    required this.getSpeed,
    required this.getSpeedAccuracyMetersPerSecond,
    required this.getTime,
    required this.getVerticalAccuracyMeters,
    required this.hasAccuracy,
    required this.hasAltitude,
    required this.hasBearing,
    required this.hasBearingAccuracy,
    required this.hasElapsedRealtimeUncertaintyNanos,
    required this.hasSpeed,
    required this.hasSpeedAccuracy,
    required this.hasVerticalAccuracy,
    required this.isComplete,
    required this.isMock,
  });

  Map<String, dynamic> toMap() {
    return {
      'ponto_id': pontoID,
      'rodada_id': rodadaID,
      'get_current_time': getCurrentTime,
      'get_accuracy': getAccuracy,
      'get_altitude': getAltitude,
      'get_bearing': getBearing,
      'get_bearing_accuracy_degrees': getBearingAccuracyDegrees,
      'get_elapsed_realtime_age_millis': getElapsedRealtimeAgeMillis,
      'get_elapsed_realtime_millis': getElapsedRealtimeMillis,
      'get_elapsed_realtime_nanos': getElapsedRealtimeNanos,
      'get_elapsed_realtime_uncertainty_nanos':
          getElapsedRealtimeUncertaintyNanos,
      'get_extras': getExtras,
      'get_latitude': getLatitude,
      'get_longitude': getLongitude,
      'get_provider': getProvider,
      'get_speed': getSpeed,
      'get_speed_accuracy_meters_per_second': getSpeedAccuracyMetersPerSecond,
      'get_time': getTime,
      'get_vertical_accuracy_meters': getVerticalAccuracyMeters,
      'has_accuracy': hasAccuracy ? 1 : 0,
      'has_altitude': hasAltitude ? 1 : 0,
      'has_bearing': hasBearing ? 1 : 0,
      'has_bearing_accuracy': hasBearingAccuracy ? 1 : 0,
      'has_elapsed_realtime_uncertainty_nanos':
          hasElapsedRealtimeUncertaintyNanos ? 1 : 0,
      'has_speed': hasSpeed ? 1 : 0,
      'has_speed_accuracy': hasSpeedAccuracy ? 1 : 0,
      'has_vertical_accuracy': hasVerticalAccuracy ? 1 : 0,
      'is_complete': isComplete ? 1 : 0,
      'is_mock': isMock ? 1 : 0,
    };
  }
}*/
