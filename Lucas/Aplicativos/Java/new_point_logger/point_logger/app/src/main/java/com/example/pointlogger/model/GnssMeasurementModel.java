package com.example.pointlogger.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;

import java.util.HashMap;
import java.util.Map;

@Entity
public class GnssMeasurementModel {
    @PrimaryKey(autoGenerate = true)
    private int gnssMeasurementID;

    @ColumnInfo(name = "ponto_id")
    private int pontoID;

    @ColumnInfo(name = "rodada_id")
    private int rodadaID;

    @ColumnInfo(name = "get_current_time")
    private String getCurrentTime;

    @ColumnInfo(name = "get_accumulated_delta_range_meters")
    private double getAccumulatedDeltaRangeMeters;

    @ColumnInfo(name = "get_accumulated_delta_range_state")
    private int getAccumulatedDeltaRangeState;

    @ColumnInfo(name = "get_accumulated_delta_range_uncertainty_meters")
    private double getAccumulatedDeltaRangeUncertaintyMeters;

    @ColumnInfo(name = "get_baseband_cn0_db_hz")
    private double getBasebandCn0DbHz;

    @ColumnInfo(name = "get_carrier_frequency_hz")
    private double getCarrierFrequencyHz;

    @ColumnInfo(name = "get_cn0_db_hz")
    private double getCn0DbHz;

    @ColumnInfo(name = "get_code_type")
    private String getCodeType;

    @ColumnInfo(name = "get_multipath_indicator")
    private int getMultipathIndicator;

    @ColumnInfo(name = "get_pseudorange_rate_meters_per_second")
    private double getPseudorangeRateMetersPerSecond;

    @ColumnInfo(name = "get_pseudorange_rate_uncertainty_meters_per_second")
    private double getPseudorangeRateUncertaintyMetersPerSecond;

    @ColumnInfo(name = "get_svid")
    private int getSvid;

    @ColumnInfo(name = "get_time_offset_nanos")
    private double getTimeOffsetNanos;

    @ColumnInfo(name = "get_constellation_type")
    private int getConstellationType;

    @ColumnInfo(name = "get_full_inter_signal_bias_nanos")
    private double getFullInterSignalBiasNanos;

    @ColumnInfo(name = "get_full_inter_signal_bias_uncertainty_nanos")
    private double getFullInterSignalBiasUncertaintyNanos;

    @ColumnInfo(name = "get_received_sv_time_nanos")
    private long getReceivedSvTimeNanos;

    @ColumnInfo(name = "get_received_sv_time_uncertainty_nanos")
    private long getReceivedSvTimeUncertaintyNanos;

    @ColumnInfo(name = "get_satellite_inter_signal_bias_nanos")
    private double getSatelliteInterSignalBiasNanos;

    @ColumnInfo(name = "get_satellite_inter_signal_bias_uncertainty_nanos")
    private double getSatelliteInterSignalBiasUncertaintyNanos;

    @ColumnInfo(name = "get_snr_in_db")
    private double getSnrInDb;

    @ColumnInfo(name = "get_state")
    private int getState;

    @ColumnInfo(name = "has_baseband_cn0_db_hz")
    private boolean hasBasebandCn0DbHz;

    @ColumnInfo(name = "has_carrier_frequency_hz")
    private boolean hasCarrierFrequencyHz;

    @ColumnInfo(name = "has_code_type")
    private boolean hasCodeType;

    @ColumnInfo(name = "has_full_inter_signal_bias_nanos")
    private boolean hasFullInterSignalBiasNanos;

    @ColumnInfo(name = "has_full_inter_signal_bias_uncertainty_nanos")
    private boolean hasFullInterSignalBiasUncertaintyNanos;

    @ColumnInfo(name = "has_satellite_inter_signal_bias_nanos")
    private boolean hasSatelliteInterSignalBiasNanos;

    @ColumnInfo(name = "has_satellite_inter_signal_bias_uncertainty_nanos")
    private boolean hasSatelliteInterSignalBiasUncertaintyNanos;

    @ColumnInfo(name = "has_snr_in_db")
    private boolean hasSnrInDb;

    public GnssMeasurementModel(int pontoID, int rodadaID, String getCurrentTime, double getAccumulatedDeltaRangeMeters,
                                int getAccumulatedDeltaRangeState, double getAccumulatedDeltaRangeUncertaintyMeters,
                                double getBasebandCn0DbHz, double getCarrierFrequencyHz, double getCn0DbHz,
                                String getCodeType, int getConstellationType, double getFullInterSignalBiasNanos,
                                double getFullInterSignalBiasUncertaintyNanos, int getMultipathIndicator,
                                double getPseudorangeRateMetersPerSecond,
                                double getPseudorangeRateUncertaintyMetersPerSecond, long getReceivedSvTimeNanos,
                                long getReceivedSvTimeUncertaintyNanos, double getSatelliteInterSignalBiasNanos,
                                double getSatelliteInterSignalBiasUncertaintyNanos, double getSnrInDb, int getState,
                                int getSvid, double getTimeOffsetNanos, boolean hasBasebandCn0DbHz,
                                boolean hasCarrierFrequencyHz, boolean hasCodeType, boolean hasFullInterSignalBiasNanos,
                                boolean hasFullInterSignalBiasUncertaintyNanos, boolean hasSatelliteInterSignalBiasNanos,
                                boolean hasSatelliteInterSignalBiasUncertaintyNanos, boolean hasSnrInDb) {
        this.pontoID = pontoID;
        this.rodadaID = rodadaID;
        this.getCurrentTime = getCurrentTime;
        this.getAccumulatedDeltaRangeMeters = getAccumulatedDeltaRangeMeters;
        this.getAccumulatedDeltaRangeState = getAccumulatedDeltaRangeState;
        this.getAccumulatedDeltaRangeUncertaintyMeters = getAccumulatedDeltaRangeUncertaintyMeters;
        this.getBasebandCn0DbHz = getBasebandCn0DbHz;
        this.getCarrierFrequencyHz = getCarrierFrequencyHz;
        this.getCn0DbHz = getCn0DbHz;
        this.getCodeType = getCodeType;
        this.getConstellationType = getConstellationType;
        this.getFullInterSignalBiasNanos = getFullInterSignalBiasNanos;
        this.getFullInterSignalBiasUncertaintyNanos = getFullInterSignalBiasUncertaintyNanos;
        this.getMultipathIndicator = getMultipathIndicator;
        this.getPseudorangeRateMetersPerSecond = getPseudorangeRateMetersPerSecond;
        this.getPseudorangeRateUncertaintyMetersPerSecond = getPseudorangeRateUncertaintyMetersPerSecond;
        this.getReceivedSvTimeNanos = getReceivedSvTimeNanos;
        this.getReceivedSvTimeUncertaintyNanos = getReceivedSvTimeUncertaintyNanos;
        this.getSatelliteInterSignalBiasNanos = getSatelliteInterSignalBiasNanos;
        this.getSatelliteInterSignalBiasUncertaintyNanos = getSatelliteInterSignalBiasUncertaintyNanos;
        this.getSnrInDb = getSnrInDb;
        this.getState = getState;
        this.getSvid = getSvid;
        this.getTimeOffsetNanos = getTimeOffsetNanos;
        this.hasBasebandCn0DbHz = hasBasebandCn0DbHz;
        this.hasCarrierFrequencyHz = hasCarrierFrequencyHz;
        this.hasCodeType = hasCodeType;
        this.hasFullInterSignalBiasNanos = hasFullInterSignalBiasNanos;
        this.hasFullInterSignalBiasUncertaintyNanos = hasFullInterSignalBiasUncertaintyNanos;
        this.hasSatelliteInterSignalBiasNanos = hasSatelliteInterSignalBiasNanos;
        this.hasSatelliteInterSignalBiasUncertaintyNanos = hasSatelliteInterSignalBiasUncertaintyNanos;
        this.hasSnrInDb = hasSnrInDb;
    }

    // Getters
    public int getGnssMeasurementID() {
        return gnssMeasurementID;
    }
    public int getPontoID() {
        return pontoID;
    }

    public int getRodadaID() {
        return rodadaID;
    }

    public String getGetCurrentTime() {
        return getCurrentTime;
    }

    public double getAccumulatedDeltaRangeMeters() {
        return getAccumulatedDeltaRangeMeters;
    }

    public int getAccumulatedDeltaRangeState() {
        return getAccumulatedDeltaRangeState;
    }

    public double getAccumulatedDeltaRangeUncertaintyMeters() {
        return getAccumulatedDeltaRangeUncertaintyMeters;
    }

    public double getBasebandCn0DbHz() {
        return getBasebandCn0DbHz;
    }

    public double getCarrierFrequencyHz() {
        return getCarrierFrequencyHz;
    }

    public double getCn0DbHz() {
        return getCn0DbHz;
    }

    public String getCodeType() {
        return getCodeType;
    }

    public int getConstellationType() {
        return getConstellationType;
    }

    public double getFullInterSignalBiasNanos() {
        return getFullInterSignalBiasNanos;
    }

    public double getFullInterSignalBiasUncertaintyNanos() {
        return getFullInterSignalBiasUncertaintyNanos;
    }

    public int getMultipathIndicator() {
        return getMultipathIndicator;
    }

    public double getPseudorangeRateMetersPerSecond() {
        return getPseudorangeRateMetersPerSecond;
    }

    public double getPseudorangeRateUncertaintyMetersPerSecond() {
        return getPseudorangeRateUncertaintyMetersPerSecond;
    }

    public long getReceivedSvTimeNanos() {
        return getReceivedSvTimeNanos;
    }

    public long getReceivedSvTimeUncertaintyNanos() {
        return getReceivedSvTimeUncertaintyNanos;
    }

    public double getSatelliteInterSignalBiasNanos() {
        return getSatelliteInterSignalBiasNanos;
    }

    public double getSatelliteInterSignalBiasUncertaintyNanos() {
        return getSatelliteInterSignalBiasUncertaintyNanos;
    }

    public double getSnrInDb() {
        return getSnrInDb;
    }

    public int getState() {
        return getState;
    }

    public int getSvid() {
        return getSvid;
    }

    public double getTimeOffsetNanos() {
        return getTimeOffsetNanos;
    }

    public boolean isHasBasebandCn0DbHz() {
        return hasBasebandCn0DbHz;
    }

    public boolean isHasCarrierFrequencyHz() {
        return hasCarrierFrequencyHz;
    }

    public boolean isHasCodeType() {
        return hasCodeType;
    }

    public boolean isHasFullInterSignalBiasNanos() {
        return hasFullInterSignalBiasNanos;
    }

    public boolean isHasFullInterSignalBiasUncertaintyNanos() {
        return hasFullInterSignalBiasUncertaintyNanos;
    }

    public boolean isHasSatelliteInterSignalBiasNanos() {
        return hasSatelliteInterSignalBiasNanos;
    }

    public boolean isHasSatelliteInterSignalBiasUncertaintyNanos() {
        return hasSatelliteInterSignalBiasUncertaintyNanos;
    }

    public boolean isHasSnrInDb() {
        return hasSnrInDb;
    }

    // Setters
    public void setGnssMeasurementID(int gnssMeasurementID) {
        this.gnssMeasurementID = gnssMeasurementID;
    }

    @Override
    public String toString() {
        return gnssMeasurementID + "," +
                pontoID + "," +
                rodadaID + "," +
                quote(getCurrentTime) + "," +
                getAccumulatedDeltaRangeMeters + "," +
                getAccumulatedDeltaRangeState + "," +
                getAccumulatedDeltaRangeUncertaintyMeters + "," +
                getBasebandCn0DbHz + "," +
                getCarrierFrequencyHz + "," +
                getCn0DbHz + "," +
                quote(getCodeType) + "," +
                getConstellationType + "," +
                getFullInterSignalBiasNanos + "," +
                getFullInterSignalBiasUncertaintyNanos + "," +
                getMultipathIndicator + "," +
                getPseudorangeRateMetersPerSecond + "," +
                getPseudorangeRateUncertaintyMetersPerSecond + "," +
                getReceivedSvTimeNanos + "," +
                getReceivedSvTimeUncertaintyNanos + "," +
                getSatelliteInterSignalBiasNanos + "," +
                getSatelliteInterSignalBiasUncertaintyNanos + "," +
                getSnrInDb + "," +
                getState + "," +
                getSvid + "," +
                getTimeOffsetNanos + "," +
                booleanToInt(hasBasebandCn0DbHz) + "," +
                booleanToInt(hasCarrierFrequencyHz) + "," +
                booleanToInt(hasCodeType) + "," +
                booleanToInt(hasFullInterSignalBiasNanos) + "," +
                booleanToInt(hasFullInterSignalBiasUncertaintyNanos) + "," +
                booleanToInt(hasSatelliteInterSignalBiasNanos) + "," +
                booleanToInt(hasSatelliteInterSignalBiasUncertaintyNanos) + "," +
                booleanToInt(hasSnrInDb);
    }

    private String quote(String str) {
        return "\"" + str + "\"";
    }

    private int booleanToInt(boolean bool) {
        return bool ? 1 : 0;
    }

    public static String getColumnHeaders() {
        return "GnssMeasurementID," +
                "PontoID," +
                "RodadaID," +
                "GetCurrentTime," +
                "GetAccumulatedDeltaRangeMeters," +
                "GetAccumulatedDeltaRangeState," +
                "GetAccumulatedDeltaRangeUncertaintyMeters," +
                "GetBasebandCn0DbHz," +
                "GetCarrierFrequencyHz," +
                "GetCn0DbHz," +
                "GetCodeType," +
                "GetConstellationType," +
                "GetFullInterSignalBiasNanos," +
                "GetFullInterSignalBiasUncertaintyNanos," +
                "GetMultipathIndicator," +
                "GetPseudorangeRateMetersPerSecond," +
                "GetPseudorangeRateUncertaintyMetersPerSecond," +
                "GetReceivedSvTimeNanos," +
                "GetReceivedSvTimeUncertaintyNanos," +
                "GetSatelliteInterSignalBiasNanos," +
                "GetSatelliteInterSignalBiasUncertaintyNanos," +
                "GetSnrInDb," +
                "GetState," +
                "GetSvid," +
                "GetTimeOffsetNanos," +
                "HasBasebandCn0DbHz," +
                "HasCarrierFrequencyHz," +
                "HasCodeType," +
                "HasFullInterSignalBiasNanos," +
                "HasFullInterSignalBiasUncertaintyNanos," +
                "HasSatelliteInterSignalBiasNanos," +
                "HasSatelliteInterSignalBiasUncertaintyNanos," +
                "HasSnrInDb";
    }

}
