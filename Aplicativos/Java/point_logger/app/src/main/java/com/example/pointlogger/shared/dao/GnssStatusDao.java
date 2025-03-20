package com.example.pointlogger.shared.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.pointlogger.model.GnssMeasurementModel;
import com.example.pointlogger.model.GnssStatusModel;

import java.util.List;

@Dao
public interface GnssStatusDao {
    @Insert
    void insert(GnssStatusModel location);

    @Query("SELECT * FROM GnssStatusModel")
    List<GnssStatusModel> getAllGnssStatus();

    @Query("SELECT * FROM GnssStatusModel LIMIT :limit")
    List<GnssStatusModel> getGnssStatusWithLimit(int limit);

    @Query("DELETE FROM GnssStatusModel")
    void deleteAll();
}
