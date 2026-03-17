package com.example.pointlogger.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;

import java.util.HashMap;
import java.util.Map;

@Entity
public class GnssStatusModel {
    @PrimaryKey(autoGenerate = true)
    private int gnssStatusID;

    @ColumnInfo(name = "ponto_id")
    private int pontoID;

    @ColumnInfo(name = "rodada_id")
    private int rodadaID;

    @ColumnInfo(name = "get_current_time")
    private String getCurrentTime;

    @ColumnInfo(name = "get_baseband_cn0_db_hz")
    private double getBasebandCn0DbHz;

    @ColumnInfo(name = "get_carrier_frequency_hz")
    private double getCarrierFrequencyHz;

    @ColumnInfo(name = "get_cn0_db_hz")
    private float getCn0DbHz;

    @ColumnInfo(name = "get_satellite_count")
    private int getSatelliteCount;

    @ColumnInfo(name = "get_svid")
    private int getSvid;

    @ColumnInfo(name = "get_constellation_type")
    private int getConstellationType;

    @ColumnInfo(name = "get_azimuth_degrees")
    private double getAzimuthDegrees;

    @ColumnInfo(name = "get_elevation_degrees")
    private double getElevationDegrees;

    @ColumnInfo(name = "has_almanac_data")
    private boolean hasAlmanacData;

    @ColumnInfo(name = "has_ephemeris_data")
    private boolean hasEphemerisData;

    @ColumnInfo(name = "has_baseband_cn_0b_hz")
    private boolean hasBasebandCn0DbHz;

    @ColumnInfo(name = "has_carrier_frequency_hz")
    private boolean hasCarrierFrequencyHz;
    @ColumnInfo(name = "used_in_fix")
    private boolean usedInFix;

    public GnssStatusModel(int pontoID, int rodadaID, String getCurrentTime, double getBasebandCn0DbHz,
                           double getCarrierFrequencyHz, float getCn0DbHz, int getSatelliteCount, int getSvid,
                           int getConstellationType, double getAzimuthDegrees, double getElevationDegrees,
                           boolean hasAlmanacData, boolean hasEphemerisData, boolean hasBasebandCn0DbHz,
                           boolean hasCarrierFrequencyHz, boolean usedInFix) {
        this.pontoID = pontoID;
        this.rodadaID = rodadaID;
        this.getCurrentTime = getCurrentTime;
        this.getBasebandCn0DbHz = getBasebandCn0DbHz;
        this.getCarrierFrequencyHz = getCarrierFrequencyHz;
        this.getCn0DbHz = getCn0DbHz;
        this.getSatelliteCount = getSatelliteCount;
        this.getSvid = getSvid;
        this.getConstellationType = getConstellationType;
        this.getAzimuthDegrees = getAzimuthDegrees;
        this.getElevationDegrees = getElevationDegrees;
        this.hasAlmanacData = hasAlmanacData;
        this.hasEphemerisData = hasEphemerisData;
        this.hasBasebandCn0DbHz = hasBasebandCn0DbHz;
        this.hasCarrierFrequencyHz = hasCarrierFrequencyHz;
        this.usedInFix = usedInFix;
    }

    // Getters públicos para os campos privados
    public int getGnssStatusID() {
        return gnssStatusID;
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

    public double getBasebandCn0DbHz() {
        return getBasebandCn0DbHz;
    }

    public double getCarrierFrequencyHz() {
        return getCarrierFrequencyHz;
    }

    public float getCn0DbHz() {
        return getCn0DbHz;
    }

    public int getSatelliteCount() {
        return getSatelliteCount;
    }

    public int getSvid() {
        return getSvid;
    }

    public int getConstellationType() {
        return getConstellationType;
    }

    public double getAzimuthDegrees() {
        return getAzimuthDegrees;
    }

    public double getElevationDegrees() {
        return getElevationDegrees;
    }

    public boolean hasAlmanacData() {
        return hasAlmanacData;
    }

    public boolean hasEphemerisData() {
        return hasEphemerisData;
    }

    public boolean hasBasebandCn0DbHz() {
        return hasBasebandCn0DbHz;
    }

    public boolean hasCarrierFrequencyHz() {
        return hasCarrierFrequencyHz;
    }

    public boolean isUsedInFix() {
        return usedInFix;
    }

    // Setters
    public void setGnssStatusID(int gnssStatusID) {
        this.gnssStatusID = gnssStatusID;
    }

    @Override
    public String toString() {
        return gnssStatusID + "," +
                pontoID + "," +
                rodadaID + "," +
                quote(getCurrentTime) + "," +
                getBasebandCn0DbHz + "," +
                getCarrierFrequencyHz + "," +
                getCn0DbHz + "," +
                getSatelliteCount + "," +
                getSvid + "," +
                getConstellationType + "," +
                getAzimuthDegrees + "," +
                getElevationDegrees + "," +
                booleanToInt(hasAlmanacData) + "," +
                booleanToInt(hasEphemerisData) + "," +
                booleanToInt(hasBasebandCn0DbHz) + "," +
                booleanToInt(hasCarrierFrequencyHz) + "," +
                booleanToInt(usedInFix);
    }

    private String quote(String str) {
        return "\"" + str + "\"";
    }

    private int booleanToInt(boolean bool) {
        return bool ? 1 : 0;
    }

    public static String getColumnHeaders() {
        return "GnssStatusID," +
                "PontoID," +
                "RodadaID," +
                "GetCurrentTime," +
                "GetBasebandCn0DbHz," +
                "GetCarrierFrequencyHz," +
                "GetCn0DbHz," +
                "GetSatelliteCount," +
                "GetSvid," +
                "GetConstellationType," +
                "GetAzimuthDegrees," +
                "GetElevationDegrees," +
                "HasAlmanacData," +
                "HasEphemerisData," +
                "HasBasebandCn0DbHz," +
                "HasCarrierFrequencyHz," +
                "UsedInFix";
    }

}
