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
                inserirLocalizacaoNoBancoDeDados(location);
                LatLng newLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                if (googleMap != null) {
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(newLatLng));
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
        TextView textViewRodada =

                new TextView(this);
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
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }

    private void limparTudo() {
        tableLayout.removeAllViews();
        rodadas.clear();
        rodadaCount = 0;
    }

    private void atualizarNumeracaoRodadas() {
        for (int i = 1; i < tableLayout.getChildCount(); i++) {
            TableRow row = (TableRow) tableLayout.getChildAt(i);
            TextView textViewRodada = (TextView) row.getChildAt(0);
            textViewRodada.setText(String.valueOf(i));
        }
        rodadaCount--;
    }

    private void iniciarColeta() {
        // Verifica se há pontos importados
        if (pontos.isEmpty()) {
            Toast.makeText(this, "Nenhum ponto importado para coleta.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Verifica se há pelo menos uma rodada definida
        if (rodadas.isEmpty()) {
            Toast.makeText(this, "Nenhuma rodada definida.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Verifica se o tempo de pausa entre as rodadas foi definido
        EditText editIntervaloPausa = findViewById(R.id.edit_intervalo_pausa);
        String intervaloPausaStr = editIntervaloPausa.getText().toString().trim();
        if (intervaloPausaStr.isEmpty()) {
            Toast.makeText(this, "Informe o tempo de pausa entre as rodadas.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Reinicia o contador de rodadas
        contadorRodadaAtual = 0;

        // Se todas as validações passaram, pode iniciar o processo de coleta
        findViewById(R.id.activity_collect_guide).setVisibility(View.GONE);
        findViewById(R.id.activity_collect_in_progress).setVisibility(View.VISIBLE);
        Button btnIniciarColeta = findViewById(R.id.btn_iniciar_processo);
        btnIniciarColeta.setVisibility(View.VISIBLE);
        TextView textTitulo = findViewById(R.id.text_coleta_status);
        textTitulo.setText("COLETA - PASSO A PASSO");
        TextView textPonto = findViewById(R.id.text_coleta_ponto);
        textPonto.setText("Ponto - " + numeroPontoAtual);
        TextView textRodada = findViewById(R.id.text_coleta_rodada);
        textRodada.setText("Rodada - " + (contadorRodadaAtual + 1)); // Atualiza o texto com o número da rodada

        // iniciarProcessoColeta(); // Remova a chamada aqui para iniciar o processo de coleta
    }

    private void iniciarProcessoColeta() {
        // Tornar o texto text_coleta_tempo visível
        TextView textTempo = findViewById(R.id.text_coleta_tempo);
        textTempo.setVisibility(View.VISIBLE);

        // Botão "Iniciar" (btn_iniciar_processo)
        Button btnIniciarProcesso = findViewById(R.id.btn_iniciar_processo);
        btnIniciarProcesso.setVisibility(View.VISIBLE);

        // Obter a localização atual do usuário
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(CollectActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(CollectActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(CollectActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_CODE);
            return;
        }

        Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (lastKnownLocation != null) {
            LatLng userLatLng = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());

            // Obter a localização do ponto da vez
            LatLng pontoLatLng = getPontoAtualLocation();

            // Calcular a distância entre o usuário e o ponto da vez
            float[] results = new float[1];
            Location.distanceBetween(userLatLng.latitude, userLatLng.longitude, pontoLatLng.latitude, pontoLatLng.longitude, results);
            float distance = results[0];

            // Verificar se a distância é menor que 1 metro
            if (distance < 10000) {
                // Atualiza o texto com o número da rodada atual
                TextView textRodada = findViewById(R.id.text_coleta_rodada);
                textRodada.setText("Rodada - " + (contadorRodadaAtual + 1));

                // Setar o título para "COLETA - EM ANDAMENTO"
                TextView textTitulo = findViewById(R.id.text_coleta_status);
                textTitulo.setText("COLETA - EM ANDAMENTO");

                // Texto "Tempo" (text_coleta_tempo)
                textTempo.setVisibility(View.VISIBLE);

                // Botão "Voltar" (btn_voltar)
                Button btnVoltar = findViewById(R.id.btn_voltar);
                btnVoltar.setVisibility(View.GONE);

                // Botão "Iniciar" (btn_iniciar_processo)
                btnIniciarProcesso.setVisibility(View.GONE);

                // Verifica se todas as rodadas foram concluídas
                if (contadorRodadaAtual + 1 > rodadas.size()) {
                    // Verificar se há mais pontos disponíveis
                    if (numeroPontoAtual + 1 <= pontos.size()) {
                        // Se houver mais pontos, atualize o número do ponto atual
                        numeroPontoAtual++;
                        // Reinicie o contador de rodadas
                        contadorRodadaAtual = 0;
                        // Atualize o texto indicando o novo ponto
                        TextView textPonto = findViewById(R.id.text_coleta_ponto);
                        textPonto.setText("Ponto - " + numeroPontoAtual);
                        textRodada.setText("Rodada - " + (contadorRodadaAtual + 1));
                        // Exibir os botões "Iniciar" e "Voltar" novamente
                        btnIniciarProcesso.setVisibility(View.VISIBLE);
                        btnVoltar.setVisibility(View.VISIBLE);
                        textTitulo.setText("COLETA - EM PAUSA");
                        return; // Saia do método para evitar execução adicional
                    } else {
                        // Se não houver mais pontos, exiba uma mensagem indicando o término da coleta
                        Toast.makeText(this, "Coleta completa!", Toast.LENGTH_SHORT).show();
                        textTitulo.setText("COLETA - FINALIZADA");
                        TextView textPonto = findViewById(R.id.text_coleta_ponto);
                        textPonto.setText("Parabéns!");
                        textRodada.setText("Consulte e exporte os dados agora.");

                        // Usando um Handler para atraso de 2 segundos antes de voltar para a tela inicial
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // Criar um intent para voltar para a tela inicial (MainActivity)
                                Intent intent = new Intent(CollectActivity.this, MainActivity.class);
                                startActivity(intent);
                                // Finalizar a atividade atual para evitar empilhamento de atividades
                                finish();
                            }
                        }, 2000); // Atraso de 2 segundos (2000 milissegundos)
                        return; // Saia do método para evitar execução adicional
                    }
                } else {
                    // Se nem todas as rodadas foram concluídas, inicie o contador para a próxima rodada
                    iniciarContador();
                }
            } else {
                // Emitir um alerta solicitando ao usuário para se aproximar mais do ponto
                Toast.makeText(CollectActivity.this, "Por favor, se aproxime mais do ponto " + numeroPontoAtual + ".", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private LatLng getPontoAtualLocation() {
        if (!pontos.isEmpty() && numeroPontoAtual <= pontos.size()) {
            PointModel pontoAtual = pontos.get(numeroPontoAtual - 1); // Ajustando índice
            return new LatLng(pontoAtual.getLatitude(), pontoAtual.getLongitude());
        } else {
            // Se não houver nenhum ponto na lista ou o número do ponto atual for inválido, retorna null
            return null;
        }
    }

    private void iniciarContador() {
        startLocationUpdates();  // Inicia a coleta de localização
        startGnssCollection();   // Supõe-se que você tenha essa funcionalidade implementada
        startGnssMeasurementService();  // Supõe-se que você tenha essa funcionalidade implementada

        Object tempoObj = rodadas.get(contadorRodadaAtual).get("tempo");
        Object unidadeTempoObj = rodadas.get(contadorRodadaAtual).get("unidadeTempo");
        if (tempoObj instanceof Integer && unidadeTempoObj instanceof String) {
            int tempoTotal = "minutos".equalsIgnoreCase((String) unidadeTempoObj) ? (Integer) tempoObj * 60 : (Integer) tempoObj;

            new CountDownTimer(tempoTotal * 1000, 1000) {
                public void onTick(long millisUntilFinished) {
                    TextView textTempo = findViewById(R.id.text_coleta_tempo);
                    textTempo.setText("Tempo restante: " + ((millisUntilFinished / 1000) + 1) + " segundos");
                }
                public void onFinish() {
                    TextView textTempo = findViewById(R.id.text_coleta_tempo);
                    textTempo.setText("Tempo restante: 0 segundos");
                    if (contadorRodadaAtual + 1 <= rodadas.size()) {
                        // Define o título e o tempo de pausa
                        TextView textTitulo = findViewById(R.id.text_coleta_status);
                        textTitulo.setText("COLETA - EM PAUSA");
                        // Define o tempo da pausa
                        EditText editIntervaloPausa = findViewById(R.id.edit_intervalo_pausa);
                        int tempoPausa = Integer.parseInt(editIntervaloPausa.getText().toString());
                        Spinner spinnerIntervaloPausa = findViewById(R.id.spinner_intervalo_pausa);
                        String unidadeTempoPausa = spinnerIntervaloPausa.getSelectedItem().toString();
                        stopLocationUpdates();  // Para a coleta de localização
                        stopGnssCollection();
                        stopGnssMeasurementService();
                        iniciarPausa(tempoPausa, unidadeTempoPausa);
                    }
                }
            }.start();
        } else {
            Toast.makeText(this, "Tempo da rodada inválido.", Toast.LENGTH_SHORT).show();
        }
    }


    private void iniciarPausa(int tempoPausa, String unidadeTempoPausa) {
        if (unidadeTempoPausa.equalsIgnoreCase("minutos")) {
            tempoPausa *= 60; // Converter minutos para segundos
        }
        stopLocationUpdates();
        new CountDownTimer(tempoPausa * 1000, 1000) {
            public void onTick(long millisUntilFinished) {
                // Atualizar o texto com o tempo restante da pausa
                TextView textTempo = findViewById(R.id.text_coleta_tempo);
                textTempo.setText("Tempo restante: " + ((millisUntilFinished / 1000)+1) + " segundos");
            }

            public void onFinish() {
                // Reinicia o processo de coleta para a próxima rodada
                contadorRodadaAtual++;
                iniciarProcessoColeta();
                TextView textTempo = findViewById(R.id.text_coleta_tempo);
                textTempo.setText("");
                System.gc();
            }
        }.start();
    }

    // Função para obter a data e hora atual formatada
    private String getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1; // Month is zero-based
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        int millisecond = calendar.get(Calendar.MILLISECOND);

        // Formatando a data e hora
        return String.format("%02d-%02d-%04d-%02d-%02d-%02d-%03d", day, month, year, hour, minute, second, millisecond);
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_CODE);
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
        LocationModel locationModel = new LocationModel(
                numeroPontoAtual, // pontoID
                contadorRodadaAtual + 1, // rodadaID
                getCurrentTime(), // getCurrentTime
                location.getAccuracy(), // getAccuracy
                location.getAltitude(), // getAltitude
                location.getBearing(), // getBearing
                location.getBearingAccuracyDegrees(), // getBearingAccuracyDegrees
                location.getElapsedRealtimeAgeMillis(), // getElapsedRealtimeAgeMillis
                location.getElapsedRealtimeMillis(), // getElapsedRealtimeMillis
                location.getElapsedRealtimeNanos(), // getElapsedRealtimeNanos
                location.getElapsedRealtimeUncertaintyNanos(), // getElapsedRealtimeUncertaintyNanos
                "", // getExtras [Problema BUNDLE] location.getExtras() ?: "", // getExtras
                location.getLatitude(), // getLatitude
                location.getLongitude(), // getLongitude
                location.getProvider(), // getProvider
                location.getSpeed(), // getSpeed
                location.getSpeedAccuracyMetersPerSecond(), // getSpeedAccuracyMetersPerSecond
                location.getTime(), // getTime
                location.getVerticalAccuracyMeters(), // getVerticalAccuracyMeters
                location.hasAccuracy(), // hasAccuracy
                location.hasAltitude(), // hasAltitude
                location.hasBearing(), // hasBearing
                location.hasBearingAccuracy(), // hasBearingAccuracy
                location.hasElapsedRealtimeUncertaintyNanos(), // hasElapsedRealtimeUncertaintyNanos
                location.hasSpeed(), // hasSpeed
                location.hasSpeedAccuracy(), // hasSpeedAccuracy
                location.hasVerticalAccuracy(), // hasVerticalAccuracy
                location.isComplete(), // isComplete
                location.isMock() // isMock
        );
        // Chamar o método do DAO para inserir dados
        myExecutor.execute(() -> {
            db.locationDao().insert(locationModel);
        });
    }


    private void startGnssCollection() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Handler handler = new Handler(Looper.getMainLooper());
        try {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 123);
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    private final GnssStatus.Callback gnssStatusCallback = new GnssStatus.Callback() {
        @Override
        public void onSatelliteStatusChanged(GnssStatus status) {
            super.onSatelliteStatusChanged(status);
            synchronized (databaseLock) {
                // Para cada satélite no status
                for (int i = 0; i < status.getSatelliteCount(); i++) {
                    // Crie um objeto GnssStatusModel
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
                    myExecutor.execute(() -> {
                        db.gnssStatusDao().insert(data);
                    });
                }
            }
        }
    };



    private void startGnssMeasurementService() {
        Log.d("CollectActivity", "GNSS Measurement Service Started");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
                return;
            }

            if (gnssMeasurementCallback == null) { // Só inicialize se ainda não foi inicializado
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

    private void handleGnssMeasurements(GnssMeasurementsEvent event) {
        new Thread(() -> {
            synchronized (databaseLock) {
                for (GnssMeasurement measurement : event.getMeasurements()) {
                    // Criação direta do modelo GnssMeasurementModel dentro do loop
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
                    myExecutor.execute(() -> db.gnssMeasurementDao().insert(data));
                }
            }
        }).start();
    }


    private void stopGnssMeasurementService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && locationManager != null && gnssMeasurementCallback != null) {
            locationManager.unregisterGnssMeasurementsCallback(gnssMeasurementCallback);
            Log.d("CollectActivity", "GNSS Measurement Service Stopped");
        }
    }

}
