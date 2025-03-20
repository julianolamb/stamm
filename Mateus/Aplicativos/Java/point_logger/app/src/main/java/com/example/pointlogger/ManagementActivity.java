package com.example.pointlogger;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.pointlogger.model.PointModel;
import com.example.pointlogger.shared.PointsProvider;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class ManagementActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private LocationManager locationManager;
    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final int PICK_XML_FILE_REQUEST_CODE = 101;
    private PointsProvider pointsProvider;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_management);

        // Inicializar o pointsProvider como um campo da classe
        pointsProvider = new PointsProvider();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_view);
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        Button importButton = findViewById(R.id.import_button);
        importButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickXmlFile();
            }
        });

        Button clearButton = findViewById(R.id.clear_button);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearPoints();
            }
        });
    }


    private void pickXmlFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("text/xml");
        startActivityForResult(intent, PICK_XML_FILE_REQUEST_CODE);
    }

    private void savePointsToSharedPreferences(List<PointModel> pointModels) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPoints", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        JSONArray jsonArray = new JSONArray();
        for (PointModel pointModel : pointModels) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("lat", pointModel.getLatitude());
                jsonObject.put("lng", pointModel.getLongitude());
                jsonObject.put("name", pointModel.getName());
                jsonArray.put(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        editor.putString("points", jsonArray.toString());
        editor.apply();
    }


    private void loadPointsFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPoints", Context.MODE_PRIVATE);
        String pointsString = sharedPreferences.getString("points", null);
        if (pointsString != null) {
            try {
                List<PointModel> pointModels = getPoints(pointsString);
                addMarkersToMap(pointModels);
            } catch (JSONException e) {
                e.printStackTrace();
            }
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

    private void addMarkersToMap(List<PointModel> pointModels) {
        googleMap.clear();
        for (PointModel pointModel : pointModels) {
            LatLng latLng = new LatLng(pointModel.getLatitude(), pointModel.getLongitude());
            googleMap.addMarker(new MarkerOptions().position(latLng).title(pointModel.getName()));
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_XML_FILE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            try {
                InputStream is = getContentResolver().openInputStream(data.getData());
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(is);

                doc.getDocumentElement().normalize();
                NodeList nodeList = doc.getElementsByTagName("point");

                // Limpar os marcadores antigos antes de adicionar novos
                googleMap.clear();

                List<PointModel> importedPointModels = new ArrayList<>();

                for (int i = 0; i < nodeList.getLength(); i++) {
                    Element element = (Element) nodeList.item(i);
                    double lat = Double.parseDouble(element.getElementsByTagName("lat").item(0).getTextContent());
                    double lng = Double.parseDouble(element.getElementsByTagName("long").item(0).getTextContent());
                    String name = element.getElementsByTagName("name").item(0).getTextContent();

                    PointModel pointModel = new PointModel(lat, lng, name);
                    importedPointModels.add(pointModel);

                    LatLng latLng = new LatLng(lat, lng);
                    googleMap.addMarker(new MarkerOptions().position(latLng).title(name));
                }

                // Salvar os pontos importados no SharedPreferences
                savePointsToSharedPreferences(importedPointModels);
                showAlertDialog("Pontos Importados", "Os pontos foram importados com sucesso.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void clearPoints() {
        googleMap.clear();
        showAlertDialog("Pontos Removidos", "Os pontos foram removidos com sucesso.");
    }

    private void showAlertDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;

        // Verifica se as permissões foram concedidas
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Se as permissões não foram concedidas, solicite ao usuário
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_CODE);
        } else {
            // Se as permissões foram concedidas, configure o mapa do Google
            googleMap.setMyLocationEnabled(true);

            // Registre o LocationListener para monitorar as atualizações de localização
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, new android.location.LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    // Não é necessário adicionar um marcador, apenas mover a câmera para a nova posição do usuário
                    LatLng newLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(newLatLng));
                }
            });

            // Movimenta a câmera para a localização do usuário
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastKnownLocation != null) {
                LatLng initialLatLng = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialLatLng, 15));
            }

            // Carrega os pontos do SharedPreferences
            loadPointsFromSharedPreferences();
        }
    }



    @Override
    protected void onResume() {
        super.onResume();
        if (googleMap != null) {
            loadPointsFromSharedPreferences();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        // Implemente a lógica para onPause
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Implemente a lógica para onDestroy
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        // Implemente a lógica para onLowMemory
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Implemente a lógica para onSaveInstanceState
    }
}
