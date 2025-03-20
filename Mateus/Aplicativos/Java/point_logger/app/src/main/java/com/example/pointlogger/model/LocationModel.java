package com.example.pointlogger.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;
import java.util.HashMap;
import java.util.Map;

@Entity
public class LocationModel {
    @PrimaryKey(autoGenerate = true)
    private int locationID;

    @ColumnInfo(name = "ponto_id")
    private int pontoID;

    @ColumnInfo(name = "rodada_id")
    private int rodadaID;

    @ColumnInfo(name = "get_current_time")
    private String getCurrentTime;

    @ColumnInfo(name = "get_accuracy")
    private double getAccuracy;

    @ColumnInfo(name = "get_altitude")
    private double getAltitude;

    @ColumnInfo(name = "get_bearing")
    private double getBearing;

    @ColumnInfo(name = "get_bearing_accuracy_degrees")
    private double getBearingAccuracyDegrees;

    @ColumnInfo(name = "get_elapsed_realtime_age_millis")
    private long getElapsedRealtimeAgeMillis;

    @ColumnInfo(name = "get_elapsed_realtime_millis")
    private long getElapsedRealtimeMillis;

    @ColumnInfo(name = "get_elapsed_realtime_nanos")
    private long getElapsedRealtimeNanos;

    @ColumnInfo(name = "get_elapsed_realtime_uncertainty_nanos")
    private double getElapsedRealtimeUncertaintyNanos;

    @ColumnInfo(name = "get_extras")
    private String getExtras;

    @ColumnInfo(name = "get_latitude")
    private double getLatitude;

    @ColumnInfo(name = "get_longitude")
    private double getLongitude;

    @ColumnInfo(name = "get_provider")
    private String getProvider;

    @ColumnInfo(name = "get_speed")
    private double getSpeed;

    @ColumnInfo(name = "get_speed_accuracy_meters_per_second")
    private double getSpeedAccuracyMetersPerSecond;

    @ColumnInfo(name = "get_time")
    private long getTime;

    @ColumnInfo(name = "get_vertical_accuracy_meters")
    private double getVerticalAccuracyMeters;

    @ColumnInfo(name = "has_accuracy")
    private boolean hasAccuracy;

    @ColumnInfo(name = "has_altitude")
    private boolean hasAltitude;

    @ColumnInfo(name = "has_bearing")
    private boolean hasBearing;

    @ColumnInfo(name = "has_bearing_accuracy")
    private boolean hasBearingAccuracy;

    @ColumnInfo(name = "has_elapsed_realtime_uncertainty_nanos")
    private boolean hasElapsedRealtimeUncertaintyNanos;

    @ColumnInfo(name = "has_speed")
    private boolean hasSpeed;

    @ColumnInfo(name = "has_speed_accuracy")
    private boolean hasSpeedAccuracy;

    @ColumnInfo(name = "has_vertical_accuracy")
    private boolean hasVerticalAccuracy;

    @ColumnInfo(name = "is_complete")
    private boolean isComplete;

    @ColumnInfo(name = "is_mock")
    private boolean isMock;

    public LocationModel(int pontoID, int rodadaID, String getCurrentTime, double getAccuracy, double getAltitude,
                         double getBearing, double getBearingAccuracyDegrees, long getElapsedRealtimeAgeMillis,
                         long getElapsedRealtimeMillis, long getElapsedRealtimeNanos,
                         double getElapsedRealtimeUncertaintyNanos, String getExtras, double getLatitude,
                         double getLongitude, String getProvider, double getSpeed,
                         double getSpeedAccuracyMetersPerSecond, long getTime,
                         double getVerticalAccuracyMeters, boolean hasAccuracy, boolean hasAltitude,
                         boolean hasBearing, boolean hasBearingAccuracy,
                         boolean hasElapsedRealtimeUncertaintyNanos, boolean hasSpeed, boolean hasSpeedAccuracy,
                         boolean hasVerticalAccuracy, boolean isComplete, boolean isMock) {
        this.pontoID = pontoID;
        this.rodadaID = rodadaID;
        this.getCurrentTime = getCurrentTime;
        this.getAccuracy = getAccuracy;
        this.getAltitude = getAltitude;
        this.getBearing = getBearing;
        this.getBearingAccuracyDegrees = getBearingAccuracyDegrees;
        this.getElapsedRealtimeAgeMillis = getElapsedRealtimeAgeMillis;
        this.getElapsedRealtimeMillis = getElapsedRealtimeMillis;
        this.getElapsedRealtimeNanos = getElapsedRealtimeNanos;
        this.getElapsedRealtimeUncertaintyNanos = getElapsedRealtimeUncertaintyNanos;
        this.getExtras = getExtras;
        this.getLatitude = getLatitude;
        this.getLongitude = getLongitude;
        this.getProvider = getProvider;
        this.getSpeed = getSpeed;
        this.getSpeedAccuracyMetersPerSecond = getSpeedAccuracyMetersPerSecond;
        this.getTime = getTime;
        this.getVerticalAccuracyMeters = getVerticalAccuracyMeters;
        this.hasAccuracy = hasAccuracy;
        this.hasAltitude = hasAltitude;
        this.hasBearing = hasBearing;
        this.hasBearingAccuracy = hasBearingAccuracy;
        this.hasElapsedRealtimeUncertaintyNanos = hasElapsedRealtimeUncertaintyNanos;
        this.hasSpeed = hasSpeed;
        this.hasSpeedAccuracy = hasSpeedAccuracy;
        this.hasVerticalAccuracy = hasVerticalAccuracy;
        this.isComplete = isComplete;
        this.isMock = isMock;
    }

    // Getters públicos para os campos privados
    public int getLocationID() {
        return locationID;
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

    public double getAccuracy() {
        return getAccuracy;
    }

    public double getAltitude() {
        return getAltitude;
    }

    public double getBearing() {
        return getBearing;
    }

    public double getBearingAccuracyDegrees() {
        return getBearingAccuracyDegrees;
    }

    public long getElapsedRealtimeAgeMillis() {
        return getElapsedRealtimeAgeMillis;
    }

    public long getElapsedRealtimeMillis() {
        return getElapsedRealtimeMillis;
    }

    public long getElapsedRealtimeNanos() {
        return getElapsedRealtimeNanos;
    }

    public double getElapsedRealtimeUncertaintyNanos() {
        return getElapsedRealtimeUncertaintyNanos;
    }

    public String getExtras() {
        return getExtras;
    }

    public double getLatitude() {
        return getLatitude;
    }

    public double getLongitude() {
        return getLongitude;
    }

    public String getProvider() {
        return getProvider;
    }

    public double getSpeed() {
        return getSpeed;
    }

    public double getSpeedAccuracyMetersPerSecond() {
        return getSpeedAccuracyMetersPerSecond;
    }

    public long getTime() {
        return getTime;
    }

    public double getVerticalAccuracyMeters() {
        return getVerticalAccuracyMeters;
    }

    public boolean hasAccuracy() {
        return hasAccuracy;
    }

    public boolean hasAltitude() {
        return hasAltitude;
    }

    public boolean hasBearing() {
        return hasBearing;
    }

    public boolean hasBearingAccuracy() {
        return hasBearingAccuracy;
    }

    public boolean hasElapsedRealtimeUncertaintyNanos() {
        return hasElapsedRealtimeUncertaintyNanos;
    }

    public boolean hasSpeed() {
        return hasSpeed;
    }

    public boolean hasSpeedAccuracy() {
        return hasSpeedAccuracy;
    }

    public boolean hasVerticalAccuracy() {
        return hasVerticalAccuracy;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public boolean isMock() {
        return isMock;
    }

    // Setters

    public void setLocationID(int locationID) {
        this.locationID = locationID;
    }
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("ponto_id", pontoID);
        map.put("rodada_id", rodadaID);
        map.put("get_current_time", getCurrentTime);
        map.put("get_accuracy", getAccuracy);
        map.put("get_altitude", getAltitude);
        map.put("get_bearing", getBearing);
        map.put("get_bearing_accuracy_degrees", getBearingAccuracyDegrees);
        map.put("get_elapsed_realtime_age_millis", getElapsedRealtimeAgeMillis);
        map.put("get_elapsed_realtime_millis", getElapsedRealtimeMillis);
        map.put("get_elapsed_realtime_nanos", getElapsedRealtimeNanos);
        map.put("get_elapsed_realtime_uncertainty_nanos", getElapsedRealtimeUncertaintyNanos);
        map.put("get_extras", getExtras != null ? getExtras : "");
        map.put("get_latitude", getLatitude);
        map.put("get_longitude", getLongitude);
        map.put("get_provider", getProvider);
        map.put("get_speed", getSpeed);
        map.put("get_speed_accuracy_meters_per_second", getSpeedAccuracyMetersPerSecond);
        map.put("get_time", getTime);
        map.put("get_vertical_accuracy_meters", getVerticalAccuracyMeters);
        map.put("has_accuracy", hasAccuracy ? 1 : 0);
        map.put("has_altitude", hasAltitude ? 1 : 0);
        map.put("has_bearing", hasBearing ? 1 : 0);
        map.put("has_bearing_accuracy", hasBearingAccuracy ? 1 : 0);
        map.put("has_elapsed_realtime_uncertainty_nanos", hasElapsedRealtimeUncertaintyNanos ? 1 : 0);
        map.put("has_speed", hasSpeed ? 1 : 0);
        map.put("has_speed_accuracy", hasSpeedAccuracy ? 1 : 0);
        map.put("has_vertical_accuracy", hasVerticalAccuracy ? 1 : 0);
        map.put("is_complete", isComplete ? 1 : 0);
        map.put("is_mock", isMock ? 1 : 0);
        return map;
    }

    @Override
    public String toString() {
        return locationID + "," +
                pontoID + "," +
                rodadaID + "," +
                quote(getCurrentTime) + "," +
                getAccuracy + "," +
                getAltitude + "," +
                getBearing + "," +
                getBearingAccuracyDegrees + "," +
                getElapsedRealtimeAgeMillis + "," +
                getElapsedRealtimeMillis + "," +
                getElapsedRealtimeNanos + "," +
                getElapsedRealtimeUncertaintyNanos + "," +
                quote(getExtras) + "," +
                getLatitude + "," +
                getLongitude + "," +
                quote(getProvider) + "," +
                getSpeed + "," +
                getSpeedAccuracyMetersPerSecond + "," +
                getTime + "," +
                getVerticalAccuracyMeters + "," +
                booleanToInt(hasAccuracy) + "," +
                booleanToInt(hasAltitude) + "," +
                booleanToInt(hasBearing) + "," +
                booleanToInt(hasBearingAccuracy) + "," +
                booleanToInt(hasElapsedRealtimeUncertaintyNanos) + "," +
                booleanToInt(hasSpeed) + "," +
                booleanToInt(hasSpeedAccuracy) + "," +
                booleanToInt(hasVerticalAccuracy) + "," +
                booleanToInt(isComplete) + "," +
                booleanToInt(isMock);
    }

    private String quote(String str) {
        return "\"" + str + "\"";
    }

    private int booleanToInt(boolean bool) {
        return bool ? 1 : 0;
    }

    public static String getColumnHeaders() {
        return "LocationID," +
                "PontoID," +
                "RodadaID," +
                "GetCurrentTime," +
                "GetAccuracy," +
                "GetAltitude," +
                "GetBearing," +
                "GetBearingAccuracyDegrees," +
                "GetElapsedRealtimeAgeMillis," +
                "GetElapsedRealtimeMillis," +
                "GetElapsedRealtimeNanos," +
                "GetElapsedRealtimeUncertaintyNanos," +
                "GetExtras," +
                "GetLatitude," +
                "GetLongitude," +
                "GetProvider," +
                "GetSpeed," +
                "GetSpeedAccuracyMetersPerSecond," +
                "GetTime," +
                "GetVerticalAccuracyMeters," +
                "HasAccuracy," +
                "HasAltitude," +
                "HasBearing," +
                "HasBearingAccuracy," +
                "HasElapsedRealtimeUncertaintyNanos," +
                "HasSpeed," +
                "HasSpeedAccuracy," +
                "HasVerticalAccuracy," +
                "IsComplete," +
                "IsMock";
    }


}
