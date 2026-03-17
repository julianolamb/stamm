/*import 'package:flutter/material.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';
import 'package:point_getter/models/point_model.dart';
import 'package:point_getter/widgets/collect/collectinprogress_widget.dart';
import 'package:provider/provider.dart';
import '../services/provider/points_provider.dart';
import '../widgets/maps/maps_widget.dart';
import '../widgets/collect/collectguide_widget.dart';

class CollectScreen extends StatefulWidget {
  @override
  _CollectScreenState createState() => _CollectScreenState();
}

class _CollectScreenState extends State<CollectScreen> {
  List<Point> _points = [];
  bool _isCollectInProgress = false;
  List<Map<String, dynamic>> _collectTimes = [];

  @override
  void initState() {
    super.initState();
    _extractPoints();
  }

  void _startCollectProgress(List<Map<String, dynamic>> collectTimes) {
    setState(() {
      _isCollectInProgress = true;
      _collectTimes = collectTimes; // Passa a lista de rodadas
    });
  }

  void _extractPoints() {
    Set<Marker> markers =
        Provider.of<PointsProvider>(context, listen: false).markers;
    _points = markers.map((marker) {
      return Point(
        name: marker.infoWindow.title!,
        latitude: marker.position.latitude,
        longitude: marker.position.longitude,
      );
    }).toList();
  }

  void _stopCollectProgress() {
    setState(() {
      _isCollectInProgress = false;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        backgroundColor: const Color.fromARGB(255, 63, 169, 6),
        title: const Text('Página de Coleta'),
        leading: IconButton(
          icon: const Icon(Icons.arrow_back),
          onPressed: () {
            Navigator.pop(context);
          },
        ),
      ),
      body: Stack(
        children: [
          Column(
            children: [
              MapsWidget(markers: Provider.of<PointsProvider>(context).markers),
              Expanded(
                child: Padding(
                  padding: const EdgeInsets.all(10.0),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.stretch,
                    children: [
                      CollectGuideWidget(
                        points: _points,
                        onStartCollectProgress: _startCollectProgress,
                        // onBack: _stopCollectProgress, // Adicione esta linha
                      ),
                    ],
                  ),
                ),
              ),
            ],
          ),
          if (_isCollectInProgress)
            Positioned(
              top: MediaQuery.of(context).size.height *
                  0.5, // Posição a ser ajustada conforme necessário
              left: 0,
              right: 0,
              bottom: 0,
              child: Container(
                color: Colors.white,
                child: CollectInProgressWidget(
                  points: _points,
                  collectTimes: _collectTimes, // Passa a lista de rodadas
                  onBack: _stopCollectProgress,
                ),
              ),
            ),
        ],
      ),
    );
  }
}*/

import 'package:flutter/material.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';
import 'package:point_getter/models/point_model.dart';
import 'package:point_getter/widgets/collect/collectinprogress_widget.dart';
import 'package:provider/provider.dart';
import '../services/provider/points_provider.dart';
import '../widgets/maps/maps_widget.dart';
import '../widgets/collect/collectguide_widget.dart';

class CollectScreen extends StatefulWidget {
  @override
  _CollectScreenState createState() => _CollectScreenState();
}

class _CollectScreenState extends State<CollectScreen> {
  List<Point> _points = [];
  bool _isCollectInProgress = false;
  List<Map<String, dynamic>> _collectTimes = [];
  int _pauseInterval = 30; // Valor padrão de pausa em segundos
  bool _isPauseInMinutes = false; // Padrão: pausa em segundos
  bool _isPaused = false;

  @override
  void initState() {
    super.initState();
    _extractPoints();
  }

  void _startCollectProgress(List<Map<String, dynamic>> collectTimes,
      int pauseInterval, bool isPauseInMinutes) {
    setState(() {
      _isCollectInProgress = true;
      _collectTimes = collectTimes; // Passa a lista de rodadas
      _pauseInterval = pauseInterval; // Atualiza o intervalo de pausa
      _isPauseInMinutes = isPauseInMinutes; // Define se a pausa é em minutos
      _isPaused = false;
    });
  }

  void _pauseCollectProgress() {
    setState(() {
      _isPaused = true; // Define como pausado
    });
  }

  void _resumeCollectProgress() {
    setState(() {
      _isPaused = false; // Retoma a coleta
    });
  }

  void _extractPoints() {
    Set<Marker> markers =
        Provider.of<PointsProvider>(context, listen: false).markers;
    _points = markers.map((marker) {
      return Point(
        name: marker.infoWindow.title!,
        latitude: marker.position.latitude,
        longitude: marker.position.longitude,
      );
    }).toList();
  }

  void _stopCollectProgress() {
    setState(() {
      _isCollectInProgress = false;
    });
  }

  void _handlePauseChanged(bool isPaused) {
    setState(() {
      _isPaused = isPaused;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        backgroundColor: const Color.fromARGB(255, 63, 169, 6),
        title: const Text('Página de Coleta'),
        leading: IconButton(
          icon: const Icon(Icons.arrow_back),
          onPressed: () {
            Navigator.pop(context);
          },
        ),
      ),
      body: SingleChildScrollView(
        child: Stack(
          children: [
            Column(
              children: [
                MapsWidget(
                    markers: Provider.of<PointsProvider>(context).markers,
                    isPaused: _isPaused),
                Padding(
                  padding: const EdgeInsets.all(10.0),
                  child: CollectGuideWidget(
                    points: _points,
                    onStartCollectProgress: _startCollectProgress,
                    //onPauseChanged: _handlePauseChanged,
                    // onBack: _stopCollectProgress, // Adicione esta linha
                  ),
                ),
              ],
            ),
            if (_isCollectInProgress)
              Positioned(
                top: MediaQuery.of(context).size.height *
                    0.5, // Posição a ser ajustada conforme necessário
                left: 0,
                right: 0,
                bottom: 0,
                child: Container(
                  color: Colors.white,
                  child: CollectInProgressWidget(
                    points: _points,
                    collectTimes: _collectTimes, // Passa a lista de rodadas
                    onBack: _stopCollectProgress,
                    pauseInterval: _pauseInterval,
                    onPauseChanged: _handlePauseChanged,
                    isPauseInMinutes: _isPauseInMinutes,
                  ),
                ),
              ),
          ],
        ),
      ),
    );
  }
}
