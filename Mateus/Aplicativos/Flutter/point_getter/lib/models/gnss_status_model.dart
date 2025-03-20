class GnssStatusModel {
  final int pontoID;
  final int rodadaID;
  final String getCurrentTime;
  final double getBasebandgetCn0DbHz;
  final double getCarrierFrequencyHz;
  final double getCn0DbHz;
  final int getSatelliteCount;
  final int getSvid;
  final int getConstellationType;
  final double getAzimuthDegrees;
  final double getElevationDegrees;
  final bool hasAlmanacData;
  final bool hasEphemerisData;
  final bool hasBasebandCn0DbHz;
  final bool hasCarrierFrequencyHz;
  final bool usedInFix;

  GnssStatusModel({
    required this.pontoID,
    required this.rodadaID,
    required this.getCurrentTime,
    required this.getBasebandgetCn0DbHz,
    required this.getCarrierFrequencyHz,
    required this.getCn0DbHz,
    required this.getSatelliteCount,
    required this.getSvid,
    required this.getConstellationType,
    required this.getAzimuthDegrees,
    required this.getElevationDegrees,
    required this.hasAlmanacData,
    required this.hasEphemerisData,
    required this.hasBasebandCn0DbHz,
    required this.hasCarrierFrequencyHz,
    required this.usedInFix,
  });

  Map<String, dynamic> toMap() {
    return {
      'ponto_id': pontoID,
      'rodada_id': rodadaID,
      'get_current_time': getCurrentTime,
      'get_baseband_cn0_db_hz': getBasebandgetCn0DbHz,
      'get_carrier_frequency_hz': getCarrierFrequencyHz,
      'get_cn0_db_hz': getCn0DbHz,
      'get_satellite_count': getSatelliteCount,
      'get_svid': getSvid,
      'get_constellation_type': getConstellationType,
      'get_azimuth_degrees': getAzimuthDegrees,
      'get_elevation_degrees': getElevationDegrees,
      'has_almanac_data': hasAlmanacData ? 1 : 0,
      'has_ephemeris_data': hasEphemerisData ? 1 : 0,
      'has_baseband_cn0_db_hz': hasBasebandCn0DbHz ? 1 : 0,
      'has_carrier_frequency_hz': hasCarrierFrequencyHz ? 1 : 0,
      'used_in_fix': usedInFix ? 1 : 0,
    };
  }
}
