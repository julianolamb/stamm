package com.example.pointlogger;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pointlogger.model.GnssMeasurementModel;
import com.example.pointlogger.model.GnssStatusModel;
import com.example.pointlogger.model.LocationModel;
import com.example.pointlogger.shared.AppDatabase;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;

public class ExportActivity extends AppCompatActivity {

    private Spinner spinnerTables;
    private TableLayout tableData;
    private Button btnClearData;
    private Button btnExportData;

    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.export_activity);

        // Initialize views
        spinnerTables = findViewById(R.id.spinner_tabela);
        tableData = findViewById(R.id.tb_dados);
        btnClearData = findViewById(R.id.btn_limpar_dados);
        btnExportData = findViewById(R.id.btn_exportar_dados);

        // Inicializar a instância do banco de dados
        db = AppDatabase.getInstance(getApplicationContext());

        // Populate spinner with table names
        populateSpinnerWithTables();

        // Set listener for spinner item selection
        spinnerTables.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Get selected option
                String selectedOption = parent.getItemAtPosition(position).toString();
                // Fetch data for the selected option
                fetchData(selectedOption);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        // Set click listener for clear data button
        btnClearData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get selected table name
                String tableName = spinnerTables.getSelectedItem().toString();
                // Clear all data from the selected table
                clearTableData(tableName);
                // Clear the table view
                tableData.removeAllViews();
            }
        });

        // Set click listener for export data button
        btnExportData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Export data and share
                exportAndShareData();
            }
        });
    }

    private void populateSpinnerWithTables() {
        // List to hold table names
        List<String> tableNames = AppDatabase.getAllTableNames();

        // Populate spinner with table names
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, tableNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTables.setAdapter(adapter);
    }

    private void fetchData(String selectedOption) {
        // Clear the table before adding new data
        tableData.removeAllViews();

        // Fetch data based on selected table name
        switch (selectedOption) {
            case "Location":
                displayLocationData();
                break;
            case "GNSS Status":
                displayGnssStatusData();
                break;
            case "GNSS Measurement":
                displayGnssMeasurementData();
                break;
        }
    }

    private void displayLocationData() {
        Executors.newSingleThreadExecutor().execute(() -> {
            // Get location data from the database with a limit of 15
            List<LocationModel> locations = db.locationDao().getLocationWithLimit(15);

            // Display location data in the table
            runOnUiThread(() -> displayDataInTable(locations));
        });
    }

    private void displayGnssStatusData() {
        Executors.newSingleThreadExecutor().execute(() -> {
            // Get GNSS status data from the database with a limit of 15
            List<GnssStatusModel> gnssStatusList = db.gnssStatusDao().getGnssStatusWithLimit(15);

            // Display GNSS status data in the table
            runOnUiThread(() -> displayDataInTable(gnssStatusList));
        });
    }

    private void displayGnssMeasurementData() {
        Executors.newSingleThreadExecutor().execute(() -> {
            // Get GNSS measurement data from the database with a limit of 15
            List<GnssMeasurementModel> gnssMeasurementList = db.gnssMeasurementDao().getGnssMeasurementWithLimit(15);

            // Display GNSS measurement data in the table
            runOnUiThread(() -> displayDataInTable(gnssMeasurementList));
        });
    }



    private <T> void displayDataInTable(List<T> dataList) {
        // Check if data list is not empty
        if (!dataList.isEmpty()) {
            // Clear existing table data
            tableData.removeAllViews();

            // Add column names as headers
            TableRow headerRow = new TableRow(this);
            for (String columnName : AppDatabase.getColumnNames(dataList.get(0).getClass())) {
                TextView headerTextView = new TextView(this);
                headerTextView.setText(columnName);
                headerRow.addView(headerTextView);

                // Add space between columns
                TextView spaceTextView = new TextView(this);
                spaceTextView.setText("   ");
                headerRow.addView(spaceTextView);
            }
            tableData.addView(headerRow);

            // Add rows with data
            for (T data : dataList) {
                TableRow dataRow = new TableRow(this);
                for (String columnName : AppDatabase.getColumnNames(data.getClass())) {
                    TextView dataTextView = new TextView(this);
                    // Verificar se o nome da coluna é booleano e ajustar a exibição
                    if (AppDatabase.getFieldValue(data, columnName) instanceof Boolean) {
                        boolean value = (Boolean) AppDatabase.getFieldValue(data, columnName);
                        dataTextView.setText(value ? "true" : "false");
                    } else {
                        dataTextView.setText(String.valueOf(AppDatabase.getFieldValue(data, columnName)));
                    }
                    dataRow.addView(dataTextView);

                    // Add space between columns
                    TextView spaceTextView = new TextView(this);
                    spaceTextView.setText(" ");
                    dataRow.addView(spaceTextView);
                }
                tableData.addView(dataRow);

                // Add space between rows
                TableRow spaceRow = new TableRow(this);
                TextView spaceTextView = new TextView(this);
                spaceTextView.setText(" ");
                spaceRow.addView(spaceTextView);
                tableData.addView(spaceRow);
            }
        } else {
            // Display message if data list is empty
            Toast.makeText(this, "No data available", Toast.LENGTH_SHORT).show();
        }
    }


    // Method to clear all data from the selected table
    private void clearTableData(String tableName) {
        switch (tableName) {
            case "Location":
                Executors.newSingleThreadExecutor().execute(() -> db.locationDao().deleteAll());
                break;
            case "GNSS Status":
                Executors.newSingleThreadExecutor().execute(() -> db.gnssStatusDao().deleteAll());
                break;
            case "GNSS Measurement":
                Executors.newSingleThreadExecutor().execute(() -> db.gnssMeasurementDao().deleteAll());
                break;
        }
    }


    private void exportAndShareData() {
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase.exportDataToCsv(ExportActivity.this);
            // Agora posta na thread principal para atualizações de UI ou para iniciar atividades
            new Handler(Looper.getMainLooper()).post(() -> {
                // Código para mostrar alguma notificação ou atualizar a UI
                Toast.makeText(ExportActivity.this, "Data export initiated", Toast.LENGTH_SHORT).show();
            });
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Implementação para lidar com solicitações de permissão (se necessário)
    }
}