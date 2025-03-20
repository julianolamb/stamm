/*import android.annotation.SuppressLint;
import android.location.GnssMeasurement;
import android.location.GnssStatus;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodChannel;

public class GnssDataHandler {
    private final FlutterEngine flutterEngine;

    public GnssDataHandler(FlutterEngine flutterEngine) {
        this.flutterEngine = flutterEngine;
    }

    public void startListening() {
        new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), "gnss_data_channel")
                .setMethodCallHandler((call, result) -> {
                    if (call.method.equals("collectGnssData")) {
                        collectGnssData(result);
                    } else {
                        result.notImplemented();
                    }
                });
    }

    @SuppressLint("MissingPermission")
    private void collectGnssData(MethodChannel.Result result) {
        LocationManager locationManager = (LocationManager) flutterEngine
                .getDartExecutor()
                .getContext()
                .getSystemService(flutterEngine.getDartExecutor().getContext().LOCATION_SERVICE);

        if (locationManager != null) {
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                GnssStatus gnssStatus = locationManager.getGnssStatus(null);
                if (gnssStatus != null) {
                    Bundle extras = new Bundle();
                    gnssStatus.fillInExtras(extras);
                    int satelliteCount = gnssStatus.getSatelliteCount();

                    Map<String, Object> data = new HashMap<>();
                    data.put("latitude", location.getLatitude());
                    data.put("longitude", location.getLongitude());
                    data.put("altitude", location.getAltitude());
                    data.put("accuracy", location.getAccuracy());
                    data.put("speed", location.getSpeed());
                    data.put("time", location.getTime());
                    data.put("satelliteCount", satelliteCount);

                    for (int i = 0; i < satelliteCount; i++) {
                        data.put("azimuth_degrees_" + i, gnssStatus.getAzimuthDegrees(i));
                        data.put("baseband_cn0_dbhz_" + i, gnssStatus.getBasebandCn0DbHz(i));
                        data.put("carrier_frequency_hz_" + i, gnssStatus.getCarrierFrequencyHz(i));
                        data.put("cn0_dbhz_" + i, gnssStatus.getCn0DbHz(i));
                        data.put("constellation_type_" + i, gnssStatus.getConstellationType(i));
                        data.put("elevation_degrees_" + i, gnssStatus.getElevationDegrees(i));
                        data.put("svid_" + i, gnssStatus.getSvid(i));
                    }

                    result.success(data);
                } else {
                    result.error("GNSS_STATUS_ERROR", "Failed to get GNSS status", null);
                }
            } else {
                result.error("LOCATION_ERROR", "Failed to get location", null);
            }
        } else {
            result.error("LOCATION_MANAGER_ERROR", "Location manager not available", null);
        }
    }
}
*/