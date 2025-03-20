import 'package:flutter/material.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';

class PointsProvider extends ChangeNotifier {
  Set<Marker> _markers = {};

  Set<Marker> get markers => _markers;

  // Método para adicionar um marcador
  void addMarker(Marker marker) {
    _markers.add(marker);
    notifyListeners(); // Notifica os ouvintes que houve uma mudança nos marcadores
  }

  // Método para limpar todos os marcadores
  void clearMarkers() {
    _markers.clear();
    notifyListeners(); // Notifica os ouvintes que houve uma mudança nos marcadores
  }
}
