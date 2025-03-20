class GnssMeasurementModel {
  final int pontoID;
  final int rodadaID;
  final String getCurrentTime;
  final double getAccumulatedDeltaRangeMeters;
  final int getAccumulatedDeltaRangeState;
  final double getAccumulatedDeltaRangeUncertaintyMeters;
  final double getBasebandCn0DbHz;
  final double getCarrierFrequencyHz;
  final double getCn0DbHz;
  final String getCodeType;
  final int getConstellationType;
  final double getFullInterSignalBiasNanos;
  final double getFullInterSignalBiasUncertaintyNanos;
  final int getMultipathIndicator;
  final double getPseudorangeRateMetersPerSecond;
  final double getPseudorangeRateUncertaintyMetersPerSecond;
  final int getReceivedSvTimeNanos;
  final int getReceivedSvTimeUncertaintyNanos;
  final double getSatelliteInterSignalBiasNanos;
  final double getSatelliteInterSignalBiasUncertaintyNanos;
  final double getSnrInDb;
  final int getState;
  final int getSvid;
  final double getTimeOffsetNanos;
  final bool hasBasebandCn0DbHz;
  final bool hasCarrierFrequencyHz;
  final bool hasCodeType;
  final bool hasFullInterSignalBiasNanos;
  final bool hasFullInterSignalBiasUncertaintyNanos;
  final bool hasSatelliteInterSignalBiasNanos;
  final bool hasSatelliteInterSignalBiasUncertaintyNanos;
  final bool hasSnrInDb;

  GnssMeasurementModel({
    required this.pontoID,
    required this.rodadaID,
    required this.getCurrentTime,
    required this.getAccumulatedDeltaRangeMeters,
    required this.getAccumulatedDeltaRangeState,
    required this.getAccumulatedDeltaRangeUncertaintyMeters,
    required this.getBasebandCn0DbHz,
    required this.getCarrierFrequencyHz,
    required this.getCn0DbHz,
    required this.getCodeType,
    required this.getConstellationType,
    required this.getFullInterSignalBiasNanos,
    required this.getFullInterSignalBiasUncertaintyNanos,
    required this.getMultipathIndicator,
    required this.getPseudorangeRateMetersPerSecond,
    required this.getPseudorangeRateUncertaintyMetersPerSecond,
    required this.getReceivedSvTimeNanos,
    required this.getReceivedSvTimeUncertaintyNanos,
    required this.getSatelliteInterSignalBiasNanos,
    required this.getSatelliteInterSignalBiasUncertaintyNanos,
    required this.getSnrInDb,
    required this.getState,
    required this.getSvid,
    required this.getTimeOffsetNanos,
    required this.hasBasebandCn0DbHz,
    required this.hasCarrierFrequencyHz,
    required this.hasCodeType,
    required this.hasFullInterSignalBiasNanos,
    required this.hasFullInterSignalBiasUncertaintyNanos,
    required this.hasSatelliteInterSignalBiasNanos,
    required this.hasSatelliteInterSignalBiasUncertaintyNanos,
    required this.hasSnrInDb,
  });

  Map<String, dynamic> toMap() {
    return {
      'ponto_id': pontoID,
      'rodada_id': rodadaID,
      'get_current_time': getCurrentTime,
      'get_accumulated_delta_range_meters': getAccumulatedDeltaRangeMeters,
      'get_accumulated_delta_range_state': getAccumulatedDeltaRangeState,
      'get_accumulated_delta_range_uncertainty_meters':
          getAccumulatedDeltaRangeUncertaintyMeters,
      'get_baseband_cn0_db_hz': getBasebandCn0DbHz,
      'get_carrier_frequency_hz': getCarrierFrequencyHz,
      'get_cn0_db_hz': getCn0DbHz,
      'get_code_type': getCodeType,
      'get_constellation_type': getConstellationType,
      'get_full_inter_signal_bias_nanos': getFullInterSignalBiasNanos,
      'get_full_inter_signal_bias_uncertainty_nanos':
          getFullInterSignalBiasUncertaintyNanos,
      'get_multipath_indicator': getMultipathIndicator,
      'get_pseudorange_rate_meters_per_second':
          getPseudorangeRateMetersPerSecond,
      'get_pseudorange_rate_uncertainty_meters_per_second':
          getPseudorangeRateUncertaintyMetersPerSecond,
      'get_received_sv_time_nanos': getReceivedSvTimeNanos,
      'get_received_sv_time_uncertainty_nanos':
          getReceivedSvTimeUncertaintyNanos,
      'get_satellite_inter_signal_bias_nanos': getSatelliteInterSignalBiasNanos,
      'get_satellite_inter_signal_bias_uncertainty_nanos':
          getSatelliteInterSignalBiasUncertaintyNanos,
      'get_snr_in_db': getSnrInDb,
      'get_state': getState,
      'get_svid': getSvid,
      'get_time_offset_nanos': getTimeOffsetNanos,
      'has_baseband_cn0_db_hz': hasBasebandCn0DbHz ? 1 : 0,
      'has_carrier_frequency_hz': hasCarrierFrequencyHz ? 1 : 0,
      'has_code_type': hasCodeType ? 1 : 0,
      'has_full_inter_signal_bias_nanos': hasFullInterSignalBiasNanos ? 1 : 0,
      'has_full_inter_signal_bias_uncertainty_nanos':
          hasFullInterSignalBiasUncertaintyNanos ? 1 : 0,
      'has_satellite_inter_signal_bias_nanos':
          hasSatelliteInterSignalBiasNanos ? 1 : 0,
      'has_satellite_inter_signal_bias_uncertainty_nanos':
          hasSatelliteInterSignalBiasUncertaintyNanos ? 1 : 0,
      'has_snr_in_db': hasSnrInDb ? 1 : 0,
    };
  }
}
