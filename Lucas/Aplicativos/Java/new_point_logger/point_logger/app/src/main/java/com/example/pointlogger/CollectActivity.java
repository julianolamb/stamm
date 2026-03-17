package com.example.pointlogger;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.GnssMeasurement;
import android.location.GnssMeasurementsEvent;
import android.location.GnssStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.pointlogger.model.GnssMeasurementModel;
import com.example.pointlogger.model.GnssStatusModel;
import com.example.pointlogger.model.LocationModel;
import com.example.pointlogger.model.PointModel;
import com.example.pointlogger.shared.AppDatabase;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class CollectActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private LocationManager locationManager;
    private static final int PERMISSION_REQUEST_CODE = 100;
    private List<PointModel> pontos = new ArrayList<>();
    private List<Map<String, Object>> rodadas = new ArrayList<>();
    private int numeroPontoAtual = 1;
    private int rodadaCount = 1;
    private int contadorRodadaAtual = 0;
    private final Object databaseLock = new Object();
    private TableLayout tableLayout;
    private AppDatabase db;
    private Executor myExecutor;
    private LocationListener locationListener;
    private GnssMeasurementsEvent.Callback gnssMeasurementCallback;
    private boolean aguardandoPrimeiraLocalizacaoParaColeta = false;
    private Handler gpsTimeoutHandler = new Handler(Looper.getMainLooper());

    private Runnable gpsTimeoutRunnable = new Runnable() {
        @Override
        public void run() {
            if (aguardandoPrimeiraLocalizacaoParaColeta) {
                aguardandoPrimeiraLocalizacaoParaColeta = false;
                Toast.makeText(CollectActivity.this, "Não foi possível obter um sinal de GPS. Tente em um local aberto.", Toast.LENGTH_LONG).show();
                findViewById(R.id.btn_iniciar_processo).setVisibility(View.VISIBLE);
                TextView textTempo = findViewById(R.id.text_coleta_tempo);
                textTempo.setText("Falha ao obter sinal de GPS.");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect);

        db = AppDatabase.getInstance(getApplicationContext());
        myExecutor = Executors.newSingleThreadExecutor();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_view);
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d("LocationDebug", "onLocationChanged FOI CHAMADO! Precisão: " + location.getAccuracy());

                if (aguardandoPrimeiraLocalizacaoParaColeta) {
                    gpsTimeoutHandler.removeCallbacks(gpsTimeoutRunnable);

                    if (location.getAccuracy() > 20) {
                        TextView textTempo = findViewById(R.id.text_coleta_tempo);
                        textTempo.setText("Aguardando sinal de GPS preciso...");
                        return;
                    }

                    aguardandoPrimeiraLocalizacaoParaColeta = false;

                    LatLng userLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    LatLng pontoLatLng = getPontoAtualLocation();

                    if (pontoLatLng == null) {
                        Toast.makeText(CollectActivity.this, "Erro: Ponto de coleta não encontrado.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    float[] results = new float[1];
                    Location.distanceBetween(userLatLng.latitude, userLatLng.longitude, pontoLatLng.latitude, pontoLatLng.longitude, results);
                    float distance = results[0];

                    if (distance < 10000) {
                        TextView textRodada = findViewById(R.id.text_coleta_rodada);
                        textRodada.setText("Rodada - " + (contadorRodadaAtual + 1));
                        TextView textTitulo = findViewById(R.id.text_coleta_status);
                        textTitulo.setText("COLETA - EM ANDAMENTO");
                        findViewById(R.id.btn_voltar).setVisibility(View.GONE);
                        iniciarContador();
                    } else {
                        Toast.makeText(CollectActivity.this, "Por favor, se aproxime mais do ponto " + numeroPontoAtual + ".", Toast.LENGTH_SHORT).show();
                        findViewById(R.id.btn_iniciar_processo).setVisibility(View.VISIBLE);
                        TextView textTempo = findViewById(R.id.text_coleta_tempo);
                        textTempo.setText("Aproxime-se do ponto e tente novamente.");
                    }
                } else {
                    inserirLocalizacaoNoBancoDeDados(location);
                }
            }
            @Override
            public void onProviderEnabled(String provider) {
                Toast.makeText(CollectActivity.this, provider + " enabled.", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onProviderDisabled(String provider) {
                Toast.makeText(CollectActivity.this, provider + " disabled.", Toast.LENGTH_SHORT).show();
            }
        };

        requestLocationUpdates();

        Spinner spinnerIntervaloPausa = findViewById(R.id.spinner_intervalo_pausa);
        Spinner spinnerTempo = findViewById(R.id.spinner_tempo);
        Button btnAdicionar = findViewById(R.id.btn_adicionar);
        Button btnLimparTudo = findViewById(R.id.btn_limpar_tudo);
        tableLayout = findViewById(R.id.table_layout);

        spinnerIntervaloPausa.setAdapter(ArrayAdapter.createFromResource(this,
                R.array.intervalo_pausa_options, android.R.layout.simple_spinner_item));

        spinnerTempo.setAdapter(ArrayAdapter.createFromResource(this,
                R.array.tempo_options, android.R.layout.simple_spinner_item));

        btnAdicionar.setOnClickListener(v -> {
            adicionarRegistro();
            limparInputETeclado();
        });

        btnLimparTudo.setOnClickListener(v -> limparTudo());

        Button btnIniciarColeta = findViewById(R.id.btn_iniciar_coleta);
        btnIniciarColeta.setOnClickListener(v -> iniciarColeta());

        Button btnIniciarProcesso = findViewById(R.id.btn_iniciar_processo);
        btnIniciarProcesso.setOnClickListener(v -> iniciarProcessoColeta());
    }

    private void requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_CODE);
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_CODE);
            return;
        }
        googleMap.setMyLocationEnabled(true);
        Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (lastKnownLocation != null) {
            LatLng initialLatLng = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialLatLng, 15));
        }
        loadPointsFromSharedPreferences();
    }

    private void iniciarProcessoColeta() {
        TextView textTempo = findViewById(R.id.text_coleta_tempo);
        textTempo.setVisibility(View.VISIBLE);
        textTempo.setText("Obtendo sinal de GPS...");

        Button btnIniciarProcesso = findViewById(R.id.btn_iniciar_processo);
        btnIniciarProcesso.setVisibility(View.GONE);

        aguardandoPrimeiraLocalizacaoParaColeta = true;

        gpsTimeoutHandler.postDelayed(gpsTimeoutRunnable, 60000); // 60 segundos de timeout
    }

    private void disableLocationOnMap() {
        if (googleMap != null) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            googleMap.setMyLocationEnabled(false);
        }
    }

    private void enableLocationOnMap() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationManager != null && locationListener != null) {
            locationManager.removeUpdates(locationListener);
        }
        gpsTimeoutHandler.removeCallbacks(gpsTimeoutRunnable);
    }

    private void loadPointsFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPoints", Context.MODE_PRIVATE);
        String pointsString = sharedPreferences.getString("points", null);
        if (pointsString != null) {
            try {
                pontos = getPoints(pointsString);
                addMarkersToMap(pontos);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void addMarkersToMap(List<PointModel> pointModels) {
        googleMap.clear();
        for (PointModel pointModel : pointModels) {
            LatLng latLng = new LatLng(pointModel.getLatitude(), pointModel.getLongitude());
            googleMap.addMarker(new MarkerOptions().position(latLng).title(pointModel.getName()));
        }
    }

    @NonNull
    private static List<PointModel> getPoints(String pointsString) throws JSONException {
        JSONArray jsonArray = new JSONArray(pointsString);
        List<PointModel> pointModels = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            double lat = jsonObject.getDouble("lat");
            double lng = jsonObject.getDouble("lng");
            String name = jsonObject.getString("name");
            PointModel pointModel = new PointModel(lat, lng, name);
            pointModels.add(pointModel);
        }
        return pointModels;
    }

    private void adicionarRegistro() {
        EditText editTempo = findViewById(R.id.edit_tempo);
        Spinner spinnerTempo = findViewById(R.id.spinner_tempo);
        String tempo = editTempo.getText().toString().trim();
        if (tempo.isEmpty()) {
            Toast.makeText(this, "Informe um tempo", Toast.LENGTH_SHORT).show();
            return;
        }
        String unidadeTempo = spinnerTempo.getSelectedItem().toString();
        String tempoCompleto = tempo + " " + unidadeTempo;
        Map<String, Object> rodada = new HashMap<>();
        rodada.put("numeroPonto", numeroPontoAtual);
        rodada.put("numeroRodada", rodadaCount);
        rodada.put("tempo", Integer.parseInt(tempo));
        rodada.put("unidadeTempo", unidadeTempo);
        rodadas.add(rodada);
        TableRow row = new TableRow(this);
        TextView textViewRodada = new TextView(this);
        textViewRodada.setId(View.generateViewId());
        textViewRodada.setText(String.valueOf(rodadaCount));
        row.addView(textViewRodada);
        TextView textViewTempo = new TextView(this);
        textViewTempo.setText(tempoCompleto);
        row.addView(textViewTempo);
        Button btnExcluir = new Button(this);
        btnExcluir.setText("X");
        btnExcluir.setOnClickListener(v -> {
            TableRow parentRow = (TableRow) v.getParent();
            tableLayout.removeView(parentRow);
            atualizarNumeracaoRodadas();
        });
        row.addView(btnExcluir);
        tableLayout.addView(row);
        rodadaCount++;
    }

    private void limparInputETeclado() {
        EditText editTempo = findViewById(R.id.edit_tempo);
        editTempo.getText().clear();
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void limparTudo() {
        tableLayout.removeAllViews();
        rodadas.clear();
        rodadaCount = 1;
    }

    private void atualizarNumeracaoRodadas() {
        for (int i = 1; i < tableLayout.getChildCount(); i++) {
            TableRow row = (TableRow) tableLayout.getChildAt(i);
            TextView textViewRodada = (TextView) row.getChildAt(0);
            textViewRodada.setText(String.valueOf(i));
        }
        rodadaCount = tableLayout.getChildCount();
    }

    private void iniciarColeta() {
        if (pontos.isEmpty()) {
            Toast.makeText(this, "Nenhum ponto importado para coleta.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (rodadas.isEmpty()) {
            Toast.makeText(this, "Nenhuma rodada definida.", Toast.LENGTH_SHORT).show();
            return;
        }
        EditText editIntervaloPausa = findViewById(R.id.edit_intervalo_pausa);
        String intervaloPausaStr = editIntervaloPausa.getText().toString().trim();
        if (intervaloPausaStr.isEmpty()) {
            Toast.makeText(this, "Informe o tempo de pausa entre as rodadas.", Toast.LENGTH_SHORT).show();
            return;
        }
        contadorRodadaAtual = 0;
        findViewById(R.id.activity_collect_guide).setVisibility(View.GONE);
        findViewById(R.id.activity_collect_in_progress).setVisibility(View.VISIBLE);
        Button btnIniciarColeta = findViewById(R.id.btn_iniciar_processo);
        btnIniciarColeta.setVisibility(View.VISIBLE);
        TextView textTitulo = findViewById(R.id.text_coleta_status);
        textTitulo.setText("COLETA - PASSO A PASSO");
        TextView textPonto = findViewById(R.id.text_coleta_ponto);
        textPonto.setText("Ponto - " + numeroPontoAtual);
        TextView textRodada = findViewById(R.id.text_coleta_rodada);
        textRodada.setText("Rodada - " + (contadorRodadaAtual + 1));
    }

    private LatLng getPontoAtualLocation() {
        if (!pontos.isEmpty() && numeroPontoAtual <= pontos.size()) {
            PointModel pontoAtual = pontos.get(numeroPontoAtual - 1);
            return new LatLng(pontoAtual.getLatitude(), pontoAtual.getLongitude());
        } else {
            return null;
        }
    }

    private void iniciarContador() {
        if (contadorRodadaAtual >= rodadas.size()) {
            handleEndOfRounds();
            return;
        }

        startLocationUpdates();
        startGnssCollection();
        startGnssMeasurementService();

        Object tempoObj = rodadas.get(contadorRodadaAtual).get("tempo");
        Object unidadeTempoObj = rodadas.get(contadorRodadaAtual).get("unidadeTempo");
        if (tempoObj instanceof Integer && unidadeTempoObj instanceof String) {
            int tempoTotal = "minutos".equalsIgnoreCase((String) unidadeTempoObj) ? (Integer) tempoObj * 60 : (Integer) tempoObj;
            new CountDownTimer(tempoTotal * 1000L, 1000) {
                public void onTick(long millisUntilFinished) {
                    TextView textTempo = findViewById(R.id.text_coleta_tempo);
                    textTempo.setText("Tempo restante: " + ((millisUntilFinished / 1000) + 1) + " segundos");
                }
                public void onFinish() {
                    TextView textTempo = findViewById(R.id.text_coleta_tempo);
                    textTempo.setText("Tempo restante: 0 segundos");
                    TextView textTitulo = findViewById(R.id.text_coleta_status);
                    textTitulo.setText("COLETA - EM PAUSA");
                    EditText editIntervaloPausa = findViewById(R.id.edit_intervalo_pausa);
                    int tempoPausa = Integer.parseInt(editIntervaloPausa.getText().toString());
                    Spinner spinnerIntervaloPausa = findViewById(R.id.spinner_intervalo_pausa);
                    String unidadeTempoPausa = spinnerIntervaloPausa.getSelectedItem().toString();
                    stopLocationUpdates();
                    stopGnssCollection();
                    stopGnssMeasurementService();
                    iniciarPausa(tempoPausa, unidadeTempoPausa);
                }
            }.start();
        } else {
            Toast.makeText(this, "Tempo da rodada inválido.", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleEndOfRounds() {
        if (numeroPontoAtual < pontos.size()) {
            numeroPontoAtual++;
            contadorRodadaAtual = 0;
            updateUiForNextPoint();
        } else {
            finalizeCollection();
        }
    }

    private void updateUiForNextPoint() {
        findViewById(R.id.btn_iniciar_processo).setVisibility(View.VISIBLE);
        findViewById(R.id.btn_voltar).setVisibility(View.VISIBLE);
        TextView textTitulo = findViewById(R.id.text_coleta_status);
        textTitulo.setText("COLETA - PRÓXIMO PONTO");
        TextView textPonto = findViewById(R.id.text_coleta_ponto);
        textPonto.setText("Ponto - " + numeroPontoAtual);
        TextView textRodada = findViewById(R.id.text_coleta_rodada);
        textRodada.setText("Rodada - " + (contadorRodadaAtual + 1));
    }

    private void finalizeCollection() {
        Toast.makeText(this, "Coleta completa!", Toast.LENGTH_SHORT).show();
        TextView textTitulo = findViewById(R.id.text_coleta_status);
        textTitulo.setText("COLETA - FINALIZADA");
        TextView textPonto = findViewById(R.id.text_coleta_ponto);
        textPonto.setText("Parabéns!");
        TextView textRodada = findViewById(R.id.text_coleta_rodada);
        textRodada.setText("Consulte e exporte os dados agora.");
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(CollectActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }, 2000);
    }

    private void iniciarPausa(int tempoPausa, String unidadeTempoPausa) {
        if (unidadeTempoPausa.equalsIgnoreCase("minutos")) {
            tempoPausa *= 60;
        }
        stopLocationUpdates();
        new CountDownTimer(tempoPausa * 1000L, 1000) {
            public void onTick(long millisUntilFinished) {
                TextView textTempo = findViewById(R.id.text_coleta_tempo);
                textTempo.setText("Pausa: " + ((millisUntilFinished / 1000)+1) + " segundos");
            }
            public void onFinish() {
                contadorRodadaAtual++;
                handleNextRoundOrPoint();
            }
        }.start();
    }

    private void handleNextRoundOrPoint() {
        if (contadorRodadaAtual < rodadas.size()) {
            iniciarProcessoColeta();
        } else {
            handleEndOfRounds();
        }
    }

    private String getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        int millisecond = calendar.get(Calendar.MILLISECOND);
        return String.format("%02d-%02d-%04d-%02d-%02d-%02d-%03d", day, month, year, hour, minute, second, millisecond);
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener, Looper.getMainLooper());
        enableLocationOnMap();
        Log.d("CollectActivity", "Location Service Collection Started");
    }

    private void stopLocationUpdates() {
        if (locationManager != null && locationListener != null) {
            locationManager.removeUpdates(locationListener);
            disableLocationOnMap();
            Log.d("CollectActivity", "Location Service Collection Stopped");
        }
    }

    private void inserirLocalizacaoNoBancoDeDados(Location location) {
        long elapsedRealtimeAgeMillis = 0;
        boolean isComplete = false;
        long elapsedRealtimeMillis = 0;
        boolean isMock = false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            elapsedRealtimeAgeMillis = location.getElapsedRealtimeAgeMillis();
            isComplete = location.isComplete();
            isMock = location.isMock();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            elapsedRealtimeMillis = location.getElapsedRealtimeMillis();
        } else {
            elapsedRealtimeMillis = location.getElapsedRealtimeNanos() / 1_000_000;
        }

        LocationModel locationModel = new LocationModel(
                numeroPontoAtual,
                contadorRodadaAtual + 1,
                getCurrentTime(),
                location.getAccuracy(),
                location.getAltitude(),
                location.getBearing(),
                location.getBearingAccuracyDegrees(),
                elapsedRealtimeAgeMillis,
                elapsedRealtimeMillis,
                location.getElapsedRealtimeNanos(),
                location.getElapsedRealtimeUncertaintyNanos(),
                "", // Extras
                location.getLatitude(),
                location.getLongitude(),
                location.getProvider(),
                location.getSpeed(),
                location.getSpeedAccuracyMetersPerSecond(),
                location.getTime(),
                location.getVerticalAccuracyMeters(),
                location.hasAccuracy(),
                location.hasAltitude(),
                location.hasBearing(),
                location.hasBearingAccuracy(),
                location.hasElapsedRealtimeUncertaintyNanos(),
                location.hasSpeed(),
                location.hasSpeedAccuracy(),
                location.hasVerticalAccuracy(),
                isComplete,
                isMock
        );
        myExecutor.execute(() -> db.locationDao().insert(locationModel));
    }

    private void startGnssCollection() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Handler handler = new Handler(Looper.getMainLooper());
        try {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            Log.d("CollectActivity", "GNSS Status Service Collection Started");
            locationManager.registerGnssStatusCallback(gnssStatusCallback, handler);
        } catch (Exception e) {
            Log.e("MainActivity", "Error registering GNSS callback: " + e);
        }
    }

    private void stopGnssCollection() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationManager.unregisterGnssStatusCallback(gnssStatusCallback);
        Log.d("CollectActivity", "GNSS Status Service Collection Stopped");
    }

    // ALTERAÇÃO: Usando inserção em lote (batching) para evitar OutOfMemoryError
    @RequiresApi(api = Build.VERSION_CODES.N)
    private final GnssStatus.Callback gnssStatusCallback = new GnssStatus.Callback() {
        @Override
        public void onSatelliteStatusChanged(GnssStatus status) {
            super.onSatelliteStatusChanged(status);
            myExecutor.execute(() -> {
                List<GnssStatusModel> statusList = new ArrayList<>();
                synchronized (databaseLock) {
                    for (int i = 0; i < status.getSatelliteCount(); i++) {
                        GnssStatusModel data = new GnssStatusModel(
                                numeroPontoAtual,
                                contadorRodadaAtual + 1,
                                getCurrentTime(),
                                status.getAzimuthDegrees(i),
                                status.getBasebandCn0DbHz(i),
                                status.getCarrierFrequencyHz(i),
                                (int) status.getCn0DbHz(i),
                                status.getConstellationType(i),
                                (int) status.getElevationDegrees(i),
                                status.getSatelliteCount(),
                                status.getSvid(i),
                                status.hasAlmanacData(i),
                                status.hasBasebandCn0DbHz(i),
                                status.hasCarrierFrequencyHz(i),
                                status.hasEphemerisData(i),
                                status.usedInFix(i)
                        );
                        statusList.add(data);
                    }
                }
                if (!statusList.isEmpty()) {
                    db.gnssStatusDao().insertAll(statusList);
                }
            });
        }
    };

    private void startGnssMeasurementService() {
        Log.d("CollectActivity", "GNSS Measurement Service Started");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            if (gnssMeasurementCallback == null) {
                gnssMeasurementCallback = new GnssMeasurementsEvent.Callback() {
                    @Override
                    public void onGnssMeasurementsReceived(GnssMeasurementsEvent event) {
                        super.onGnssMeasurementsReceived(event);
                        handleGnssMeasurements(event);
                    }
                };
            }
            locationManager.registerGnssMeasurementsCallback(gnssMeasurementCallback, new Handler(Looper.getMainLooper()));
        } else {
            Log.e("CollectActivity", "GNSS Measurement not supported on this device");
        }
    }

    // ALTERAÇÃO: Usando inserção em lote (batching) para evitar OutOfMemoryError
    private void handleGnssMeasurements(GnssMeasurementsEvent event) {
        myExecutor.execute(() -> {
            List<GnssMeasurementModel> measurementList = new ArrayList<>();
            synchronized (databaseLock) {
                for (GnssMeasurement measurement : event.getMeasurements()) {
                    GnssMeasurementModel data = new GnssMeasurementModel(
                            numeroPontoAtual,
                            contadorRodadaAtual + 1,
                            getCurrentTime(),
                            measurement.getAccumulatedDeltaRangeMeters(),
                            measurement.getAccumulatedDeltaRangeState(),
                            measurement.getAccumulatedDeltaRangeUncertaintyMeters(),
                            measurement.getBasebandCn0DbHz(),
                            measurement.getCarrierFrequencyHz(),
                            measurement.getCn0DbHz(),
                            measurement.getCodeType(),
                            measurement.getConstellationType(),
                            measurement.getFullInterSignalBiasNanos(),
                            measurement.getFullInterSignalBiasUncertaintyNanos(),
                            measurement.getMultipathIndicator(),
                            measurement.getPseudorangeRateMetersPerSecond(),
                            measurement.getPseudorangeRateUncertaintyMetersPerSecond(),
                            measurement.getReceivedSvTimeNanos(),
                            measurement.getReceivedSvTimeUncertaintyNanos(),
                            measurement.getSatelliteInterSignalBiasNanos(),
                            measurement.getSatelliteInterSignalBiasUncertaintyNanos(),
                            measurement.getSnrInDb(),
                            measurement.getState(),
                            measurement.getSvid(),
                            measurement.getTimeOffsetNanos(),
                            measurement.hasBasebandCn0DbHz(),
                            measurement.hasCarrierFrequencyHz(),
                            measurement.hasCodeType(),
                            measurement.hasFullInterSignalBiasNanos(),
                            measurement.hasFullInterSignalBiasUncertaintyNanos(),
                            measurement.hasSatelliteInterSignalBiasNanos(),
                            measurement.hasSatelliteInterSignalBiasUncertaintyNanos(),
                            measurement.hasSnrInDb()
                    );
                    measurementList.add(data);
                }
            }
            if (!measurementList.isEmpty()) {
                db.gnssMeasurementDao().insertAll(measurementList);
            }
        });
    }

    private void stopGnssMeasurementService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && locationManager != null && gnssMeasurementCallback != null) {
            locationManager.unregisterGnssMeasurementsCallback(gnssMeasurementCallback);
            Log.d("CollectActivity", "GNSS Measurement Service Stopped");
        }
    }
}