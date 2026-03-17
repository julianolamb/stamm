package com.example.pointlogger.shared.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.pointlogger.model.GnssStatusModel;
import com.example.pointlogger.model.LocationModel;

import java.util.List;

@Dao
public interface LocationDao {
    @Insert
    void insert(LocationModel location);

    // ADICIONE ESTE MÉTODO
    @Insert
    void insertAll(List<GnssStatusModel> gnssStatusList);

    @Query("SELECT * FROM LocationModel")
    List<LocationModel> getAllLocations();

    @Query("SELECT * FROM LocationModel LIMIT :limit")
    List<LocationModel> getLocationWithLimit(int limit);

    @Query("DELETE FROM LocationModel")
    void deleteAll();
}
