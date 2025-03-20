package com.example.pointlogger.shared.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.pointlogger.model.GnssMeasurementModel;

import java.util.List;

@Dao
public interface GnssMeasurementDao {
    @Insert
    void insert(GnssMeasurementModel location);

    @Query("SELECT * FROM GnssMeasurementModel")
    List<GnssMeasurementModel> getAllGnssMeasurement();

    @Query("SELECT * FROM GnssMeasurementModel LIMIT :limit")
    List<GnssMeasurementModel> getGnssMeasurementWithLimit(int limit);

    @Query("DELETE FROM GnssMeasurementModel")
    void deleteAll();
}
