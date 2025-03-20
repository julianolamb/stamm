package com.example.point_getter

import androidx.annotation.RequiresApi
import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.annotation.NonNull
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import java.util.concurrent.Executors
import android.location.GnssStatus
import android.location.GnssMeasurement
import android.location.GnssMeasurementsEvent
import android.location.LocationManager
import android.util.Log
import java.util.Calendar


class MainActivity : FlutterActivity() {

    //private val LOCATION_CHANNEL = "location_channel"
    //private val LOCATION_DATA_STREAM = "location_data_stream"
    //private var locationDataEvents: EventChannel.EventSink? = null
    //private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val GNSS_CHANNEL = "gnss_channel"
    private val GNSS_DATA_STREAM = "gnss_data_stream"
    private var gnssDataEvents: EventChannel.EventSink? = null

    private val GNSS_MEASUREMENT_CHANNEL = "gnss_measurement_channel"
    private val GNSS_MEASUREMENT_DATA_STREAM = "gnss_measurement_data_stream"
    private var gnssMeasurementDataEvents: EventChannel.EventSink? = null
    private var gnssMeasurementCallback: GnssMeasurementsEvent.Callback? = null

    // Função para obter a data e hora atual formatada
    private fun getCurrentTime(): String {
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH) + 1 // Month is zero-based
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)
    val second = calendar.get(Calendar.SECOND)
    val millisecond = calendar.get(Calendar.MILLISECOND)
    
    // Formatando a data e hora
    return String.format("%02d-%02d-%04d-%02d-%02d-%02d-%03d", day, month, year, hour, minute, second, millisecond)
}

    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)

        /*val locationMethodChannel = MethodChannel(flutterEngine.dartExecutor.binaryMessenger, LOCATION_CHANNEL)
        locationMethodChannel.setMethodCallHandler { call: MethodCall, result: Result ->
            if (call.method == "startLocationUpdates") {
                startLocationUpdates()
                result.success(null)
            } else if (call.method == "stopLocationUpdates") {
                stopLocationUpdates()
                result.success(null)
            } else {
                result.notImplemented()
            }
        }

        val locationEventChannel = EventChannel(flutterEngine.dartExecutor.binaryMessenger, LOCATION_DATA_STREAM)
        locationEventChannel.setStreamHandler(object : EventChannel.StreamHandler {
            override fun onListen(arguments: Any?, events: EventChannel.EventSink) {
                locationDataEvents = events
                startLocationService()
            }

            override fun onCancel(arguments: Any?) {
                stopLocationService()
            }
        })*/

        //fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val gnssMeasurementMethodChannel = MethodChannel(flutterEngine.dartExecutor.binaryMessenger, GNSS_MEASUREMENT_CHANNEL)
        gnssMeasurementMethodChannel.setMethodCallHandler { call: MethodCall, result: Result ->
            if (call.method == "startGnssMeasurementService") {
                startGnssMeasurementService()
                result.success(null)
            } else if (call.method == "stopGnssMeasurementService") {
                stopGnssMeasurementService()
                result.success(null)
            } else {
                result.notImplemented()
            }
        }

        val gnssMeasurementEventChannel = EventChannel(flutterEngine.dartExecutor.binaryMessenger, GNSS_MEASUREMENT_DATA_STREAM)
        gnssMeasurementEventChannel.setStreamHandler(object : EventChannel.StreamHandler {
            override fun onListen(arguments: Any?, events: EventChannel.EventSink) {
                gnssMeasurementDataEvents = events
                startGnssMeasurementService()
                Log.d("MainActivity", "GNSS Measurement Service Started")
            }

            override fun onCancel(arguments: Any?) {
                stopGnssMeasurementService()
            }
        })

        val gnssMethodChannel = MethodChannel(flutterEngine.dartExecutor.binaryMessenger, GNSS_CHANNEL)
        gnssMethodChannel.setMethodCallHandler { call: MethodCall, result: Result ->
            when (call.method) {
                "startGnssCollection" -> {
                    startGnssCollection()
                    result.success(null)
                }
                "stopGnssCollection" -> {
                    stopGnssCollection()
                    result.success(null)
                }
                else -> result.notImplemented()
            }
        }

        val gnssEventChannel = EventChannel(flutterEngine.dartExecutor.binaryMessenger, GNSS_DATA_STREAM)
        gnssEventChannel.setStreamHandler(object : EventChannel.StreamHandler {
            override fun onListen(arguments: Any?, events: EventChannel.EventSink) {
                gnssDataEvents = events
                startGnssService()
            }

            override fun onCancel(arguments: Any?) {
                stopGnssService()
            }
        })
    }

    /*private fun startLocationUpdates() {
        val locationRequest = LocationRequest.create().apply {
            interval = 0L
            fastestInterval = 0L
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val cancellationTokenSource = CancellationTokenSource()
        val locationCallback = object : com.google.android.gms.location.LocationCallback() {
            override fun onLocationResult(locationResult: com.google.android.gms.location.LocationResult) {
                val location = locationResult.lastLocation
                val locationData = if (location != null) {
                    mapOf(
                        "getCurrentTime" to getCurrentTime(),
                        "getAccuracy" to location.getAccuracy(),
                        "getAltitude" to location.getAltitude(),
                        "getBearing" to location.getBearing(),
                        "getBearingAccuracyDegrees" to location.getBearingAccuracyDegrees(),
                        "getElapsedRealtimeAgeMillis" to location.getElapsedRealtimeAgeMillis(),
                        "getElapsedRealtimeMillis" to location.getElapsedRealtimeMillis(),
                        "getElapsedRealtimeNanos" to location.getElapsedRealtimeNanos(),
                        "getElapsedRealtimeUncertaintyNanos" to location.getElapsedRealtimeUncertaintyNanos(),
                        "getExtras" to (location.getExtras() ?: ""),
                        "getLatitude" to location.getLatitude(),
                        "getLongitude" to location.getLongitude(),
                        //"getMslAltitudeAccuracyMeters" to location.getMslAltitudeAccuracyMeters(),
                        //"getMslAltitudeMeters" to location.getMslAltitudeMeters(),
                        "getProvider" to (location.getProvider() ?: ""),
                        "getSpeed" to location.getSpeed(),
                        "getSpeedAccuracyMetersPerSecond" to location.getSpeedAccuracyMetersPerSecond(),
                        "getTime" to location.getTime(),
                        "getVerticalAccuracyMeters" to location.getVerticalAccuracyMeters(),
                        "hasAltitude" to location.hasAltitude(),
                        "hasBearing" to location.hasBearing(),
                        "hasAccuracy" to location.hasAccuracy(),
                        "hasBearingAccuracy" to location.hasBearingAccuracy(),
                        "hasElapsedRealtimeUncertaintyNanos" to location.hasElapsedRealtimeUncertaintyNanos(),
                        //"hasMslAltitude" to location.hasMslAltitude(),
                        //"hasMslAltitudeAccuracy" to location.hasMslAltitudeAccuracy(),
                        "hasSpeed" to location.hasSpeed(),
                        "hasSpeedAccuracy" to location.hasSpeedAccuracy(),
                        "hasVerticalAccuracy" to location.hasVerticalAccuracy(),
                        "isComplete" to location.isComplete(),
                        "isMock" to location.isMock(),
                    )
                } else {
                    emptyMap()
                }
                locationDataEvents?.success(locationData)
            }
        }

        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        } catch (e: SecurityException) {
            // Handle permission errors
        }
    }

    private fun stopLocationUpdates() {
        //fusedLocationClient.removeLocationUpdates(locationCallback)
        Log.d("MainActivity", "Location Service Stopped")
    }

    private fun startLocationService() {
        startLocationUpdates()
        Log.d("MainActivity", "Location Service Started")
    }

    private fun stopLocationService() {
        stopLocationUpdates()
    }*/

    private fun startGnssCollection() {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        val handler = Handler(Looper.getMainLooper())
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 123)
                return
            }
            locationManager.registerGnssStatusCallback(gnssStatusCallback, handler)
        } catch (e: Exception) {
            Log.e("MainActivity", "Error registering GNSS callback: $e")
        }
    }

    private fun stopGnssCollection() {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        locationManager.unregisterGnssStatusCallback(gnssStatusCallback)

        Log.d("MainActivity", "GNSS Status Service Stopped")
    }

    private fun startGnssService() {
        startGnssCollection()
        Log.d("MainActivity", "GNSS Status Service Started")
    }

    private fun stopGnssService() {
        stopGnssCollection()
    }

    private fun startGnssMeasurementService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 123)
                return
            }
    
            var localGnssMeasurementCallback = gnssMeasurementCallback // Store the current value locally
            if (localGnssMeasurementCallback == null) { // Initialize only if not already initialized
                localGnssMeasurementCallback = object : GnssMeasurementsEvent.Callback() {
                    override fun onGnssMeasurementsReceived(event: GnssMeasurementsEvent) {
                        super.onGnssMeasurementsReceived(event)
                        handleGnssMeasurements(event)
                    }
                }
                gnssMeasurementCallback = localGnssMeasurementCallback // Update the property value
            }
    
            val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
            val handler = Handler(Looper.getMainLooper())
            locationManager.registerGnssMeasurementsCallback(localGnssMeasurementCallback, handler)
        } else {
            Log.e("MainActivity", "GNSS Measurement not supported on this device")
        }
    }
    
    private fun stopGnssMeasurementService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val localGnssMeasurementCallback = gnssMeasurementCallback // Store the current value locally
            if (localGnssMeasurementCallback != null) {
                val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
                locationManager.unregisterGnssMeasurementsCallback(localGnssMeasurementCallback)
                gnssMeasurementCallback = null // Clear the reference after unregistering
                Log.d("MainActivity", "GNSS Measurement Service Stopped")
            }
        }
    }
    
    
    private fun handleGnssMeasurements(event: GnssMeasurementsEvent) {
        val measurements = event.measurements
        val measurementDataList = measurements.map { measurement ->
            // Map GNSS measurement data
            mapOf(
                "getCurrentTime" to getCurrentTime(),
                "getAccumulatedDeltaRangeMeters" to measurement.getAccumulatedDeltaRangeMeters(),
                "getAccumulatedDeltaRangeState" to measurement.getAccumulatedDeltaRangeState(),
                "getAccumulatedDeltaRangeUncertaintyMeters" to measurement.getAccumulatedDeltaRangeUncertaintyMeters(),
                "getBasebandCn0DbHz" to measurement.getBasebandCn0DbHz(),
                "getCarrierFrequencyHz" to measurement.getCarrierFrequencyHz(),
                "getCn0DbHz" to measurement.getCn0DbHz(),
                "getCodeType" to measurement.getCodeType(),
                "getConstellationType" to measurement.getConstellationType(),
                "getFullInterSignalBiasNanos" to measurement.getFullInterSignalBiasNanos(),
                "getFullInterSignalBiasUncertaintyNanos" to measurement.getFullInterSignalBiasUncertaintyNanos(),
                "getMultipathIndicator" to measurement.getMultipathIndicator(),
                "getPseudorangeRateMetersPerSecond" to measurement.getPseudorangeRateMetersPerSecond(),
                "getPseudorangeRateUncertaintyMetersPerSecond" to measurement.getPseudorangeRateUncertaintyMetersPerSecond(),
                "getReceivedSvTimeNanos" to measurement.getReceivedSvTimeNanos(),
                "getReceivedSvTimeUncertaintyNanos" to measurement.getReceivedSvTimeUncertaintyNanos(),
                "getSatelliteInterSignalBiasNanos" to measurement.getSatelliteInterSignalBiasNanos(),
                "getSatelliteInterSignalBiasUncertaintyNanos" to measurement.getSatelliteInterSignalBiasUncertaintyNanos(),
                "getSnrInDb" to measurement.getSnrInDb(),
                "getState" to measurement.getState(),
                "getSvid" to measurement.getSvid(),
                "getTimeOffsetNanos" to measurement.getTimeOffsetNanos(),
                "hasBasebandCn0DbHz" to measurement.hasBasebandCn0DbHz(),
                "hasCarrierFrequencyHz" to measurement.hasCarrierFrequencyHz(),
                "hasCodeType" to measurement.hasCodeType(),
                "hasFullInterSignalBiasNanos" to measurement.hasFullInterSignalBiasNanos(),
                "hasFullInterSignalBiasUncertaintyNanos" to measurement.hasFullInterSignalBiasUncertaintyNanos(),
                "hasSatelliteInterSignalBiasNanos" to measurement.hasSatelliteInterSignalBiasNanos(),
                "hasSatelliteInterSignalBiasUncertaintyNanos" to measurement.hasSatelliteInterSignalBiasUncertaintyNanos(),
                "hasSnrInDb" to measurement.hasSnrInDb()
            )
        }
    
        // Send GNSS measurement data to Flutter
        gnssMeasurementDataEvents?.success(measurementDataList)
    
        // Log received GNSS measurement data
        //Log.d("MainActivity", "GNSS Measurement Data Received: $measurementDataList")
    }
    
    

    @RequiresApi(Build.VERSION_CODES.N)
    private val gnssStatusCallback = object : GnssStatus.Callback() {
        override fun onSatelliteStatusChanged(status: GnssStatus) {
            super.onSatelliteStatusChanged(status)
            val satellites = mutableListOf<Map<String, Any?>>()
            for (i in 0 until status.satelliteCount) {
                val satellite = mapOf(
                    "getCurrentTime" to getCurrentTime(),
                    "getBasebandCn0DbHz" to status.getBasebandCn0DbHz(i),
                    "getCarrierFrequencyHz" to status.getCarrierFrequencyHz(i),
                    "getCn0DbHz" to status.getCn0DbHz(i),
                    "getSatelliteCount" to status.getSatelliteCount(),
                    "getSvid" to status.getSvid(i),
                    "getConstellationType" to status.getConstellationType(i),
                    "getAzimuthDegrees" to status.getAzimuthDegrees(i),
                    "getElevationDegrees" to status.getElevationDegrees(i),
                    "hasAlmanacData" to status.hasAlmanacData(i),
                    "hasEphemerisData" to status.hasEphemerisData(i),
                    "hasCarrierFrequencyHz" to status.hasCarrierFrequencyHz(i),
                    "hasBasebandCn0DbHz" to status.hasBasebandCn0DbHz(i),
                    "usedInFix" to status.usedInFix(i)
                )
                satellites.add(satellite)
            }
            gnssDataEvents?.success(satellites)
        }
    }
}
