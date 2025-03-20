/*import 'dart:io';
import 'package:flutter/material.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';
import 'package:xml/xml.dart' as xml;
import 'package:file_picker/file_picker.dart';
import '../widgets/maps/maps_widget.dart'; // Importe o MapsWidget

class ManagementScreen extends StatefulWidget {
  @override
  _ManagementScreenState createState() => _ManagementScreenState();
}

class _ManagementScreenState extends State<ManagementScreen> {
  GoogleMapController? _controller;
  Set<Marker> _markers = {}; // Conjunto de marcadores no mapa

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        backgroundColor: const Color.fromARGB(255, 63, 169, 6),
        title: const Text('Página de Gerenciamento'),
        leading: IconButton(
          icon: const Icon(Icons.arrow_back),
          onPressed: () {
            Navigator.pop(context);
          },
        ),
      ),
      body: Column(
        children: [
          MapsWidget(
            controller: _controller,
            markers: _markers,
          ),
          Expanded(
            flex: 2,
            child: Padding(
              padding: const EdgeInsets.all(10.0),
              child: Row(
                mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                children: [
                  ElevatedButton(
                    onPressed: _importPoints,
                    style: ElevatedButton.styleFrom(
                      backgroundColor: Colors.green, // Cor verde
                      shape: RoundedRectangleBorder(
                        borderRadius:
                            BorderRadius.circular(20), // Borda arredondada
                      ),
                      minimumSize: Size(
                        MediaQuery.of(context).size.width * 0.4,
                        100, // Altura do botão
                      ),
                    ),
                    child: const Text(
                      'Importar Pontos',
                      style: TextStyle(fontSize: 18, color: Colors.black),
                    ),
                  ),
                  ElevatedButton(
                    onPressed: _clearMarkers,
                    style: ElevatedButton.styleFrom(
                      backgroundColor: Colors.red, // Cor vermelha
                      shape: RoundedRectangleBorder(
                        borderRadius:
                            BorderRadius.circular(20), // Borda arredondada
                      ),
                      minimumSize: Size(
                        MediaQuery.of(context).size.width * 0.4,
                        100, // Altura do botão
                      ),
                    ),
                    child: const Text(
                      'Limpar Pontos',
                      style: TextStyle(fontSize: 18, color: Colors.black),
                    ),
                  ),
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }

  Future<void> _importPoints() async {
    try {
      // Solicitar ao usuário para selecionar o arquivo XML
      FilePickerResult? result = await FilePicker.platform.pickFiles(
        type: FileType.custom,
        allowedExtensions: ['xml'],
      );

      if (result != null) {
        File file = File(result.files.single.path!);
        String fileContent = await file.readAsString();

        // Analisar o conteúdo do arquivo XML
        xml.XmlDocument document = xml.XmlDocument.parse(fileContent);
        List<xml.XmlElement> points =
            document.findAllElements('point').toList();

        // Limpar os marcadores antigos
        _clearMarkers();

        // Adicionar novos marcadores com base nos dados do arquivo XML
        points.forEach((point) {
          double latitude = double.parse(point.findElements('lat').single.text);
          double longitude =
              double.parse(point.findElements('long').single.text);
          String name = point.findElements('name').single.text;

          _markers.add(
            Marker(
              markerId: MarkerId(name),
              position: LatLng(latitude, longitude),
              infoWindow: InfoWindow(title: name),
            ),
          );
        });

        // Atualizar o mapa para exibir os novos marcadores
        setState(
            () {}); // Notifica o Flutter sobre a atualização dos marcadores
      }
    } catch (e) {
      print('Erro ao importar pontos: $e');
    }
  }

  void _clearMarkers() {
    setState(() {
      _markers.clear();
    });
  }
}
*/
import 'dart:io';
import 'package:flutter/material.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';
import 'package:xml/xml.dart' as xml;
import 'package:file_picker/file_picker.dart';
import '../widgets/maps/maps_widget.dart';
import 'package:provider/provider.dart';
import 'package:point_getter/services/provider/points_provider.dart';

class ManagementScreen extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        backgroundColor: const Color.fromARGB(255, 63, 169, 6),
        title: const Text('Página de Gerenciamento'),
        leading: IconButton(
          icon: const Icon(Icons.arrow_back),
          onPressed: () {
            Navigator.pop(context);
          },
        ),
      ),
      body: Column(
        children: [
          MapsWidget(
            markers: Provider.of<PointsProvider>(context)
                .markers, // Usando os marcadores do PointsProvider
            isPaused: false,
          ),
          Expanded(
            child: Padding(
              padding: const EdgeInsets.all(10.0),
              child: Container(
                decoration: BoxDecoration(
                  color: Colors.grey[200], // Cor cinza claro
                  borderRadius: BorderRadius.circular(
                      20.0), // Bordas arredondadas do container
                ),
                child: Column(
                  mainAxisAlignment: MainAxisAlignment.start,
                  crossAxisAlignment: CrossAxisAlignment.stretch,
                  children: [
                    const Center(
                      child: Text(
                        'Gerência de Pontos',
                        style: TextStyle(fontSize: 24, color: Colors.black),
                      ),
                    ),
                    const SizedBox(height: 20),
                    ElevatedButton(
                      onPressed: () => _importPoints(
                          context), // Passando o contexto para a função
                      style: ElevatedButton.styleFrom(
                        backgroundColor: Colors.green, // Cor verde
                        shape: RoundedRectangleBorder(
                          borderRadius:
                              BorderRadius.circular(20), // Borda arredondada
                        ),
                        minimumSize: Size(
                          MediaQuery.of(context).size.width * 0.7,
                          100, // Altura do botão
                        ),
                      ),
                      child: const Text(
                        'Importar Pontos',
                        style: TextStyle(fontSize: 20, color: Colors.black),
                      ),
                    ),
                    const SizedBox(height: 10),
                    ElevatedButton(
                      onPressed: () => _clearMarkers(
                          context), // Passando o contexto para a função
                      style: ElevatedButton.styleFrom(
                        backgroundColor: Colors.red, // Cor vermelha
                        shape: RoundedRectangleBorder(
                          borderRadius:
                              BorderRadius.circular(20), // Borda arredondada
                        ),
                        minimumSize: Size(
                          MediaQuery.of(context).size.width * 0.7,
                          100, // Altura do botão
                        ),
                      ),
                      child: const Text(
                        'Limpar Pontos',
                        style: TextStyle(fontSize: 20, color: Colors.black),
                      ),
                    ),
                  ],
                ),
              ),
            ),
          ),
        ],
      ),
    );
  }

  void _importPoints(BuildContext context) async {
    try {
      // Solicitar ao usuário para selecionar o arquivo XML
      FilePickerResult? result = await FilePicker.platform.pickFiles(
        type: FileType.custom,
        allowedExtensions: ['xml'],
      );

      if (result != null) {
        File file = File(result.files.single.path!);
        String fileContent = await file.readAsString();

        // Analisar o conteúdo do arquivo XML
        xml.XmlDocument document = xml.XmlDocument.parse(fileContent);
        List<xml.XmlElement> points =
            document.findAllElements('point').toList();

        // Limpar os marcadores antigos
        _clearMarkers(context);

        // Adicionar novos marcadores com base nos dados do arquivo XML
        points.forEach((point) {
          double latitude = double.parse(point.findElements('lat').single.text);
          double longitude =
              double.parse(point.findElements('long').single.text);
          String name = point.findElements('name').single.text;

          Provider.of<PointsProvider>(context, listen: false).addMarker(
            Marker(
              markerId: MarkerId(name),
              position: LatLng(latitude, longitude),
              infoWindow: InfoWindow(title: name),
            ),
          );
        });
      }
    } catch (e) {
      print('Erro ao importar pontos: $e');
    }
  }

  void _clearMarkers(BuildContext context) {
    Provider.of<PointsProvider>(context, listen: false).clearMarkers();
  }
}
