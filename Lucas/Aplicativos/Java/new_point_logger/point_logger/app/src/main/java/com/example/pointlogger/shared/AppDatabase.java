package com.example.pointlogger.shared;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.FileProvider;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.pointlogger.model.GnssMeasurementModel;
import com.example.pointlogger.model.GnssStatusModel;
import com.example.pointlogger.model.LocationModel;
import com.example.pointlogger.shared.dao.GnssMeasurementDao;
import com.example.pointlogger.shared.dao.GnssStatusDao;
import com.example.pointlogger.shared.dao.LocationDao;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {LocationModel.class, GnssStatusModel.class, GnssMeasurementModel.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase INSTANCE;

    public abstract LocationDao locationDao();
    public abstract GnssStatusDao gnssStatusDao();
    public abstract GnssMeasurementDao gnssMeasurementDao();

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "gnss_data.db")
                            .fallbackToDestructiveMigration() // Handle migrations
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    // Método para obter os nomes das colunas de uma tabela
    public static List<String> getColumnNames(Class<?> entityClass) {
        List<String> columnNames = new ArrayList<>();
        Field[] fields = entityClass.getDeclaredFields();
        for (Field field : fields) {
            String columnName = field.getName();
            columnNames.add(columnName);
        }
        return columnNames;
    }

    public static <T> Object getFieldValue(T object, String fieldName) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(object);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Método para obter o nome da tabela associada a uma entidade
    private static String getTableName(Class<?> entityClass) {
        if (entityClass == LocationModel.class) {
            return "tb_location";
        } else if (entityClass == GnssStatusModel.class) {
            return "tb_gnss_status";
        } else if (entityClass == GnssMeasurementModel.class) {
            return "tb_gnss_measurement";
        } else {
            return "";
        }
    }

    public void closeDatabase() {
        if (INSTANCE != null) {
            INSTANCE.close();
            INSTANCE = null;
        }
    }

    public static List<String> getAllTableNames() {
        List<String> tableNames = new ArrayList<>();
        tableNames.add("Location");
        tableNames.add("GNSS Status");
        tableNames.add("GNSS Measurement");
        return tableNames;
    }

    /*public static void exportDataToCsv(Context context) {
        File file = null;
        try {
            file = File.createTempFile("data_export", ".csv", context.getCacheDir());

            FileWriter writer = new FileWriter(file);

            AppDatabase db = getInstance(context); // Get instance of database

            exportTableData("Location Data", db.locationDao().getAllLocations(), writer);
            exportTableData("Gnss Status Data", db.gnssStatusDao().getAllGnssStatus(), writer);
            exportTableData("Gnss Measurement Data", db.gnssMeasurementDao().getAllGnssMeasurement(), writer);

            writer.flush();
            writer.close();

            shareExportedData(context, file);
        } catch (IOException e) {
            Log.e("AppDatabase", "Error exporting data: " + e.getMessage());
            Toast.makeText(context, "Failed to export data: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }*/

    public static void exportDataToCsv(Context context) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            File file = new File(context.getCacheDir(), "data_export.csv");
            try (FileWriter writer = new FileWriter(file)) {
                AppDatabase db = AppDatabase.getInstance(context);

                // Exporta os dados de cada tabela
                exportTableData("Location Data", db.locationDao().getAllLocations(), writer);
                exportTableData("Gnss Status Data", db.gnssStatusDao().getAllGnssStatus(), writer);
                exportTableData("Gnss Measurement Data", db.gnssMeasurementDao().getAllGnssMeasurement(), writer);

                writer.flush();
                shareExportedData(context, file);
            } catch (IOException e) {
                Log.e("AppDatabase", "Error exporting data: " + e.getMessage());
                Handler mainHandler = new Handler(Looper.getMainLooper());
                mainHandler.post(() -> Toast.makeText(context, "Failed to export data: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        });
    }

    private static void exportTableData(String header, List<?> dataList, FileWriter writer) throws IOException {
        writer.append(header).append("\n");
        for (Object data : dataList) {
            // Escreve cada linha de dados
            writer.append(data.toString()).append("\n");
        }
        writer.append("\n"); // Adiciona uma linha em branco entre as tabelas
    }


    private static void shareExportedData(Context context, File file) {
        Uri contentUri = FileProvider.getUriForFile(context, "com.example.pointlogger.provider", file);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/csv");
        shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(Intent.createChooser(shareIntent, "Share CSV via"));
    }
}
