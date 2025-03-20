/*import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:point_getter/models/point_model.dart';
import 'package:point_getter/widgets/collect/collectinprogress_widget.dart';
import 'package:provider/provider.dart';
import '../../services/provider/points_provider.dart';

class CollectGuideWidget extends StatefulWidget {
  final List<Point> points;
  final Function(List<Map<String, dynamic>>) onStartCollectProgress;

  CollectGuideWidget({
    required this.points,
    required this.onStartCollectProgress,
  });

  @override
  _CollectGuideWidgetState createState() => _CollectGuideWidgetState();
}

class _CollectGuideWidgetState extends State<CollectGuideWidget> {
  List<Map<String, dynamic>> _collectTimes = [];
  TextEditingController _timeController = TextEditingController();
  bool _isMinutes = false;

  void _extractPoints() {
    // Implemente aqui a lógica para extrair os pontos
  }

  @override
  Widget build(BuildContext context) {
    bool hasCollectTimes = _collectTimes.isNotEmpty;
    bool hasPoints = widget.points.isNotEmpty;

    return Expanded(
      child: SingleChildScrollView(
        child: Padding(
          padding: const EdgeInsets.all(20.0),
          child: Column(
            children: [
              const Text(
                'Passo a Passo - COLETA',
                style: TextStyle(fontSize: 24, fontWeight: FontWeight.bold),
              ),
              const SizedBox(height: 8),
              Text(
                'Para começar, defina as rodadas com os tempos de coleta abaixo.',
                style: TextStyle(fontSize: 14, color: Colors.grey[700]),
                textAlign: TextAlign.center,
              ),
              const SizedBox(height: 20),
              _buildTimeInput(),
              const SizedBox(height: 20),
              _buildCollectTimesTable(),
              const SizedBox(height: 20),
              Row(
                children: [
                  Expanded(
                    child: Padding(
                      padding: const EdgeInsets.only(left: 16.0),
                      child: ElevatedButton(
                        onPressed: () {
                          setState(() {
                            _collectTimes.clear();
                          });
                        },
                        style: ElevatedButton.styleFrom(
                          backgroundColor: Colors.red,
                          minimumSize:
                              const Size.fromHeight(50), // Aumenta a altura
                        ),
                        child: const Text(
                          'Limpar tudo',
                          style: TextStyle(
                              color: Colors.black), // Cor do texto preto
                        ),
                      ),
                    ),
                  ),
                  const SizedBox(width: 20), // Espaço entre os botões
                  Expanded(
                    child: Padding(
                      padding: const EdgeInsets.all(10.0),
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.stretch,
                        children: [
                          ElevatedButton(
                            onPressed: hasPoints
                                ? () {
                                    if (hasCollectTimes) {
                                      widget.onStartCollectProgress(
                                          _collectTimes);
                                    } else {
                                      _showNoCollectTimesAlert(context);
                                    }
                                  }
                                : _showNoPointsAlert,
                            child: const Text('Iniciar Coleta'),
                          ),
                        ],
                      ),
                    ),
                  ),
                ],
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildTimeInput() {
    return Row(
      children: [
        Expanded(
          child: TextField(
            controller: _timeController,
            keyboardType: TextInputType.number,
            inputFormatters: [FilteringTextInputFormatter.digitsOnly],
            decoration: const InputDecoration(
              labelText: "Tempo",
              border: OutlineInputBorder(),
            ),
          ),
        ),
        const SizedBox(width: 10),
        Container(
          decoration: BoxDecoration(
            border: Border.all(color: Colors.grey),
            borderRadius: BorderRadius.circular(5),
          ),
          child: DropdownButton<bool>(
            value: _isMinutes,
            items: const [
              DropdownMenuItem(
                value: false,
                child: Text("Segundos"),
              ),
              DropdownMenuItem(
                value: true,
                child: Text("Minutos"),
              ),
            ],
            onChanged: (value) {
              setState(() {
                _isMinutes = value!;
              });
            },
          ),
        ),
        const SizedBox(width: 10),
        Container(
          decoration: BoxDecoration(
            border: Border.all(color: Colors.grey),
            borderRadius: BorderRadius.circular(5),
          ),
          child: IconButton(
            icon: const Icon(Icons.add),
            onPressed: () {
              setState(() {
                final int index = _collectTimes.length +
                    1; // Atribui o próximo índice disponível
                _collectTimes.add({
                  'index': index, // Adiciona o índice ao mapa
                  'time': int.parse(_timeController.text),
                  'isMinutes': _isMinutes,
                });
                _timeController.clear();
              });
            },
          ),
        ),
      ],
    );
  }

  Widget _buildCollectTimesTable() {
    return Table(
      columnWidths: const {
        0: FlexColumnWidth(0.8),
        1: FlexColumnWidth(1.2),
        2: FlexColumnWidth(0.8),
      },
      border: TableBorder.all(color: Colors.grey),
      children: [
        const TableRow(
          children: [
            Padding(
              padding: EdgeInsets.all(8.0),
              child: Center(
                child: Text(
                  'Rodada',
                  style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
                ),
              ),
            ),
            Padding(
              padding: EdgeInsets.all(8.0),
              child: Center(
                child: Text(
                  'Tempo',
                  style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
                ),
              ),
            ),
            Padding(
              padding: EdgeInsets.all(8.0),
              child: Center(
                child: Text(
                  'Excluir',
                  style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
                ),
              ),
            ),
          ],
        ),
        ..._collectTimes.asMap().entries.map((entry) {
          final index = entry.key + 1;
          final timeData = entry.value;
          final time = timeData['time'];
          final isMinutes = timeData['isMinutes'];
          return TableRow(
            children: [
              Padding(
                padding: const EdgeInsets.all(8.0),
                child: Center(
                  child: Text(
                    '$index',
                    style: const TextStyle(fontSize: 16),
                  ),
                ),
              ),
              Padding(
                padding: const EdgeInsets.all(8.0),
                child: Center(
                  child: Text(
                    '$time ${isMinutes ? 'Minutos' : 'Segundos'}.',
                    style: const TextStyle(fontSize: 16),
                  ),
                ),
              ),
              Padding(
                padding: const EdgeInsets.all(8.0),
                child: IconButton(
                  icon: const Icon(Icons.close),
                  onPressed: () {
                    setState(() {
                      _collectTimes.removeAt(entry.key);
                    });
                  },
                ),
              ),
            ],
          );
        }).toList(),
      ],
    );
  }

  void _showNoCollectTimesAlert(BuildContext context) {
    showDialog(
      context: context,
      builder: (BuildContext context) {
        return AlertDialog(
          title: Text('Nenhuma Rodada Definida'),
          content: Text(
              'Por favor, defina pelo menos uma rodada para iniciar a coleta.'),
          actions: [
            TextButton(
              onPressed: () {
                Navigator.of(context).pop();
              },
              child: Text('OK'),
            ),
          ],
        );
      },
    );
  }

  void _showNoPointsAlert() {
    showDialog(
      context: context,
      builder: (BuildContext context) {
        return AlertDialog(
          title: Text('Nenhum Ponto Importado'),
          content: Text(
              'Por favor, importe pelo menos um ponto para prosseguir com a coleta.'),
          actions: [
            TextButton(
              onPressed: () {
                Navigator.of(context).pop();
              },
              child: Text('OK'),
            ),
          ],
        );
      },
    );
  }
}*/

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:point_getter/models/point_model.dart';
import 'package:point_getter/widgets/collect/collectinprogress_widget.dart';
import 'package:provider/provider.dart';
import '../../services/provider/points_provider.dart';

class CollectGuideWidget extends StatefulWidget {
  final List<Point> points;
  final Function(List<Map<String, dynamic>>, int, bool) onStartCollectProgress;
  //final Function(bool) onPauseChanged;

  CollectGuideWidget({
    required this.points,
    required this.onStartCollectProgress,
    //required this.onPauseChanged, // Adicionado
  });

  @override
  _CollectGuideWidgetState createState() => _CollectGuideWidgetState();
}

class _CollectGuideWidgetState extends State<CollectGuideWidget> {
  List<Map<String, dynamic>> _collectTimes = [];
  TextEditingController _timeController = TextEditingController();
  TextEditingController _pauseIntervalController = TextEditingController();
  bool _isRoundMinutes = false; // Renomeada para diferenciar das rodadas
  bool _isPauseMinutes =
      false; // Nova variável para controlar minutos no intervalo de pausa

  /*void _handlePauseChanged(bool isPaused) {
    widget.onPauseChanged(isPaused);
  }*/

  void _extractPoints() {
    // Implemente aqui a lógica para extrair os pontos
  }

  @override
  Widget build(BuildContext context) {
    bool hasCollectTimes = _collectTimes.isNotEmpty;
    bool hasPoints = widget.points.isNotEmpty;

    return Expanded(
      child: SingleChildScrollView(
        child: Padding(
          padding: const EdgeInsets.all(20.0),
          child: Column(
            children: [
              const Text(
                'Passo a Passo - COLETA',
                style: TextStyle(fontSize: 24, fontWeight: FontWeight.bold),
              ),
              const SizedBox(height: 20),
              const Text(
                'Para começar, defina as rodadas com os tempos de coleta abaixo.',
                style: TextStyle(fontSize: 14, color: Colors.black),
                textAlign: TextAlign.center,
              ),
              const SizedBox(height: 8),
              _buildPauseIntervalInput(),
              const SizedBox(height: 20),
              _buildTimeInput(),
              const SizedBox(height: 20),
              _buildCollectTimesTable(),
              const SizedBox(height: 20),
              Row(
                children: [
                  Expanded(
                    child: Padding(
                      padding: const EdgeInsets.only(left: 16.0),
                      child: ElevatedButton(
                        onPressed: () {
                          setState(() {
                            _collectTimes.clear();
                          });
                        },
                        style: ElevatedButton.styleFrom(
                          backgroundColor: Colors.red,
                          minimumSize:
                              const Size.fromHeight(50), // Aumenta a altura
                        ),
                        child: const Text(
                          'Limpar tudo',
                          style: TextStyle(
                              color: Colors.black), // Cor do texto preto
                        ),
                      ),
                    ),
                  ),
                  const SizedBox(width: 20), // Espaço entre os botões
                  Expanded(
                    child: Padding(
                      padding: const EdgeInsets.all(10.0),
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.stretch,
                        children: [
                          ElevatedButton(
                            onPressed: hasPoints
                                ? () {
                                    if (hasCollectTimes) {
                                      widget.onStartCollectProgress(
                                          _collectTimes,
                                          int.parse(
                                              _pauseIntervalController.text),
                                          _isPauseMinutes);
                                    } else {
                                      _showNoCollectTimesAlert(context);
                                    }
                                  }
                                : _showNoPointsAlert,
                            child: const Text('Iniciar Coleta'),
                          ),
                        ],
                      ),
                    ),
                  ),
                ],
              ),
            ],
          ),
        ),
      ),
    );
  }

  /*@override
  Widget build(BuildContext context) {
    bool hasCollectTimes = _collectTimes.isNotEmpty;
    bool hasPoints = widget.points.isNotEmpty;

    return Expanded(
      child: SingleChildScrollView(
        child: Padding(
          padding: const EdgeInsets.all(20.0),
          child: Column(
            children: [
              const Text(
                'Passo a Passo - COLETA',
                style: TextStyle(fontSize: 24, fontWeight: FontWeight.bold),
              ),
              const SizedBox(height: 20),
              const Text(
                'Para começar, defina as rodadas com os tempos de coleta abaixo.',
                style: TextStyle(fontSize: 14, color: Colors.black),
                textAlign: TextAlign.center,
              ),
              const SizedBox(height: 8),
              _buildPauseIntervalInput(),
              const SizedBox(height: 20),
              _buildTimeInput(),
              const SizedBox(height: 20),
              _buildCollectTimesTable(),
              const SizedBox(height: 20),
              Row(
                children: [
                  Expanded(
                    child: Padding(
                      padding: const EdgeInsets.only(left: 16.0),
                      child: ElevatedButton(
                        onPressed: () {
                          setState(() {
                            _collectTimes.clear();
                          });
                        },
                        style: ElevatedButton.styleFrom(
                          backgroundColor: Colors.red,
                          minimumSize:
                              const Size.fromHeight(50), // Aumenta a altura
                        ),
                        child: const Text(
                          'Limpar tudo',
                          style: TextStyle(
                              color: Colors.black), // Cor do texto preto
                        ),
                      ),
                    ),
                  ),
                  const SizedBox(width: 20), // Espaço entre os botões
                  Expanded(
                    child: Padding(
                      padding: const EdgeInsets.all(10.0),
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.stretch,
                        children: [
                          ElevatedButton(
                            onPressed: hasPoints
                                ? () {
                                    if (hasCollectTimes) {
                                      widget.onStartCollectProgress(
                                          _collectTimes,
                                          int.parse(
                                              _pauseIntervalController.text),
                                          _isPauseMinutes);
                                    } else {
                                      _showNoCollectTimesAlert(context);
                                    }
                                  }
                                : _showNoPointsAlert,
                            child: const Text('Iniciar Coleta'),
                          ),
                        ],
                      ),
                    ),
                  ),
                ],
              ),
            ],
          ),
        ),
      ),
    );
  }*/

  Widget _buildPauseIntervalInput() {
    return Row(
      children: [
        Expanded(
          child: TextField(
            controller: _pauseIntervalController,
            keyboardType: TextInputType.number,
            inputFormatters: [FilteringTextInputFormatter.digitsOnly],
            decoration: const InputDecoration(
              labelText: "Intervalo de Pausa",
              border: OutlineInputBorder(),
            ),
          ),
        ),
        const SizedBox(width: 10),
        Container(
          decoration: BoxDecoration(
            border: Border.all(color: Colors.grey),
            borderRadius: BorderRadius.circular(5),
          ),
          child: DropdownButton<bool>(
            value: _isPauseMinutes, // Atualizada para _isPauseMinutes
            items: const [
              DropdownMenuItem(
                value: false,
                child: Text("Segundos"),
              ),
              DropdownMenuItem(
                value: true,
                child: Text("Minutos"),
              ),
            ],
            onChanged: (value) {
              setState(() {
                _isPauseMinutes = value!;
              });
            },
          ),
        ),
      ],
    );
  }

  Widget _buildTimeInput() {
    return Row(
      children: [
        Expanded(
          child: TextField(
            controller: _timeController,
            keyboardType: TextInputType.number,
            inputFormatters: [FilteringTextInputFormatter.digitsOnly],
            decoration: const InputDecoration(
              labelText: "Tempo",
              border: OutlineInputBorder(),
            ),
          ),
        ),
        const SizedBox(width: 10),
        Container(
          decoration: BoxDecoration(
            border: Border.all(color: Colors.grey),
            borderRadius: BorderRadius.circular(5),
          ),
          child: DropdownButton<bool>(
            value:
                _isRoundMinutes, // Mantida para controlar minutos nas rodadas
            items: const [
              DropdownMenuItem(
                value: false,
                child: Text("Segundos"),
              ),
              DropdownMenuItem(
                value: true,
                child: Text("Minutos"),
              ),
            ],
            onChanged: (value) {
              setState(() {
                _isRoundMinutes = value!;
              });
            },
          ),
        ),
        const SizedBox(width: 10),
        Container(
          decoration: BoxDecoration(
            border: Border.all(color: Colors.grey),
            borderRadius: BorderRadius.circular(5),
          ),
          child: IconButton(
            icon: const Icon(Icons.add),
            onPressed: () {
              setState(() {
                final int index = _collectTimes.length +
                    1; // Atribui o próximo índice disponível
                _collectTimes.add({
                  'index': index, // Adiciona o índice ao mapa
                  'time': int.parse(_timeController.text),
                  'isMinutes': _isRoundMinutes, // Mantida para as rodadas
                });
                _timeController.clear();
              });
            },
          ),
        ),
      ],
    );
  }

  Widget _buildCollectTimesTable() {
    return Table(
      columnWidths: const {
        0: FlexColumnWidth(0.8),
        1: FlexColumnWidth(1.2),
        2: FlexColumnWidth(0.8),
      },
      border: TableBorder.all(color: Colors.grey),
      children: [
        const TableRow(
          children: [
            Padding(
              padding: EdgeInsets.all(8.0),
              child: Center(
                child: Text(
                  'Rodada',
                  style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
                ),
              ),
            ),
            Padding(
              padding: EdgeInsets.all(8.0),
              child: Center(
                child: Text(
                  'Tempo',
                  style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
                ),
              ),
            ),
            Padding(
              padding: EdgeInsets.all(8.0),
              child: Center(
                child: Text(
                  'Excluir',
                  style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
                ),
              ),
            ),
          ],
        ),
        ..._collectTimes.asMap().entries.map((entry) {
          final index = entry.key + 1;
          final timeData = entry.value;
          final time = timeData['time'];
          final isMinutes = timeData['isMinutes'];
          return TableRow(
            children: [
              Padding(
                padding: const EdgeInsets.all(8.0),
                child: Center(
                  child: Text(
                    '$index',
                    style: const TextStyle(fontSize: 16),
                  ),
                ),
              ),
              Padding(
                padding: const EdgeInsets.all(8.0),
                child: Center(
                  child: Text(
                    '$time ${isMinutes ? 'Minutos' : 'Segundos'}.',
                    style: const TextStyle(fontSize: 16),
                  ),
                ),
              ),
              Padding(
                padding: const EdgeInsets.all(8.0),
                child: IconButton(
                  icon: const Icon(Icons.close),
                  onPressed: () {
                    setState(() {
                      _collectTimes.removeAt(entry.key);
                    });
                  },
                ),
              ),
            ],
          );
        }).toList(),
      ],
    );
  }

  void _showNoCollectTimesAlert(BuildContext context) {
    showDialog(
      context: context,
      builder: (BuildContext context) {
        return AlertDialog(
          title: Text('Nenhuma Rodada Definida'),
          content: Text(
              'Por favor, defina pelo menos uma rodada para iniciar a coleta.'),
          actions: [
            TextButton(
              onPressed: () {
                Navigator.of(context).pop();
              },
              child: Text('OK'),
            ),
          ],
        );
      },
    );
  }

  void _showNoPointsAlert() {
    showDialog(
      context: context,
      builder: (BuildContext context) {
        return AlertDialog(
          title: Text('Nenhum Ponto Importado'),
          content: Text(
              'Por favor, importe pelo menos um ponto para prosseguir com a coleta.'),
          actions: [
            TextButton(
              onPressed: () {
                Navigator.of(context).pop();
              },
              child: Text('OK'),
            ),
          ],
        );
      },
    );
  }
}
