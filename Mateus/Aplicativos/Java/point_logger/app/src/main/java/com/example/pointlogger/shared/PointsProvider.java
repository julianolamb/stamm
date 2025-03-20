package com.example.pointlogger.shared;

import com.example.pointlogger.model.PointModel;

import java.util.ArrayList;
import java.util.List;

public class PointsProvider {
    private List<PointModel> pointModels = new ArrayList<>();

    public void addPoint(PointModel pointModel) {
        pointModels.add(pointModel);
    }

    public void removePoint(PointModel pointModel) {
        pointModels.remove(pointModel);
    }

    public List<PointModel> getPoints() {
        return pointModels;
    }

    public void addAllPoints(List<PointModel> pointsToAdd) {
        pointModels.addAll(pointsToAdd);
    }

}
