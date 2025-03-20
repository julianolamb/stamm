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

    @Query("SELECT * FROM LocationModel")
    List<LocationModel> getAllLocations();

    @Query("SELECT * FROM LocationModel LIMIT :limit")
    List<LocationModel> getLocationWithLimit(int limit);

    @Query("DELETE FROM LocationModel")
    void deleteAll();
}
