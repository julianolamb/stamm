import 'dart:convert';
import 'dart:io';
import 'package:csv/csv.dart';
import 'package:excel/excel.dart';
import 'package:flutter/material.dart';
import 'package:path_provider/path_provider.dart';
import 'package:point_getter/services/provider/database_provider.dart';
import 'package:share_plus/share_plus.dart';

class ExportScreen extends StatefulWidget {
  @override
  _ExportScreenState createState() => _ExportScreenState();
}

class _ExportScreenState extends State<ExportScreen> {
  late List<Map<String, dynamic>> _currentLocationDataList = [];
  late String _selectedOption = 'Location'; // Opção padrão selecionada
  bool _dataLoaded = false;
  int _rowsPerPage = 10;
  int _currentPage = 0;
  int _totalItems = 0;

  @override
  void initState() {
    super.initState();
    _fetchAllData();
  }

  Future<void> _fetchAllData() async {
    try {
      final String table = _selectedOption == 'Location'
          ? 'tb_location'
          : _selectedOption == 'GNSS Status'
              ? 'tb_gnss_status'
              : 'tb_gnss_measurement';

      final String query = 'SELECT COUNT(*) FROM $table';
      final totalRows = await DatabaseProvider.rawQuery(query);
      _totalItems = totalRows[0]['COUNT(*)'];

      // Carregar a primeira página de dados
      await _fetchDataForPage(0);
      setState(() {
        _dataLoaded = true;
      });
    } catch (error) {
      print('Erro ao buscar dados: $error');
    }
  }

  Future<void> _fetchDataForPage(int page) async {
    try {
      final String table = _selectedOption == 'Location'
          ? 'tb_location'
          : _selectedOption == 'GNSS Status'
              ? 'tb_gnss_status'
              : 'tb_gnss_measurement';

      final String query =
          'SELECT * FROM $table LIMIT $_rowsPerPage OFFSET ${page * _rowsPerPage}';
      final List<Map<String, dynamic>> dataList =
          await DatabaseProvider.rawQuery(query);
      setState(() {
        _currentLocationDataList = dataList;
      });
    } catch (error) {
      print('Erro ao buscar dados da página $page: $error');
    }
  }

  Future<void> _clearData() async {
    try {
      final String table = _selectedOption == 'Location'
          ? 'tb_location'
          : _selectedOption == 'GNSS Status'
              ? 'tb_gnss_status'
              : 'tb_gnss_measurement';

      await DatabaseProvider.rawQuery('DELETE FROM $table');
      await _fetchAllData();
      // Mostrar alerta de sucesso
    } catch (error) {
      print('Erro ao limpar dados: $error');
    }
  }

  /*Future<void> _exportData() async {
    try {
      final Directory? downloadsDirectory = await getExternalStorageDirectory();
      if (downloadsDirectory != null) {
        final String currentDateTime =
            DateTime.now().toString().replaceAll(RegExp(r'[: -]'), '');
        final String filePath =
            '${downloadsDirectory.path}/data_$currentDateTime.csv';

        // Escrever no arquivo CSV
        final File file = File(filePath);
        await _writeCsvFile(file);

        // Compartilhar o arquivo
        Share.shareXFiles([XFile('filePath')]);
      } else {
        print('Erro ao obter o diretório de downloads.');
      }
    } catch (error) {
      print('Erro ao exportar dados: $error');
      // Exibir o alerta de erro
      showDialog(
        context: context,
        builder: (BuildContext context) {
          return AlertDialog(
            title: Text('Erro ao exportar dados'),
            content: Text('Ocorreu um erro ao exportar os dados: $error'),
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

  Future<void> _writeCsvFile(File file) async {
    final List<int> bytes = const Utf8Encoder().convert('\uFEFF');
    await file.writeAsBytes(bytes, mode: FileMode.writeOnlyAppend);
    final IOSink sink = file.openWrite(mode: FileMode.append);

    for (final row in _currentLocationDataList) {
      sink.writeln(row.values.map((data) => '"$data"').join(','));
    }

    await sink.flush();
    await sink.close();
  }*/

  Future<void> _exportData() async {
    try {
      final Directory? downloadsDirectory = await getExternalStorageDirectory();
      if (downloadsDirectory != null) {
        final String currentDateTime =
            DateTime.now().toString().replaceAll(RegExp(r'[: -]'), '');
        final String filePath =
            '${downloadsDirectory.path}/data_$currentDateTime.csv';

        // Escrever no arquivo CSV
        final File file = File(filePath);
        await _writeCsvFile(file);

        // Compartilhar o arquivo
        Share.shareFiles([filePath]);
        // Mostrar alerta de sucesso
        /*showDialog(
          context: context,
          builder: (BuildContext context) {
            return AlertDialog(
              title: Text('Exportação concluída'),
              content: Text(
                  'Os dados foram exportados com sucesso para:\n$filePath'),
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
        );*/
      } else {
        print('Erro ao obter o diretório de downloads.');
      }
    } catch (error) {
      print('Erro ao exportar dados: $error');
      // Exibir o alerta de erro
      showDialog(
        context: context,
        builder: (BuildContext context) {
          return AlertDialog(
            title: Text('Erro ao exportar dados'),
            content: Text('Ocorreu um erro ao exportar os dados: $error'),
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

  Future<void> _writeCsvFile(File file) async {
    final List<int> bytes = const Utf8Encoder().convert('\uFEFF');
    await file.writeAsBytes(bytes, mode: FileMode.writeOnlyAppend);
    final IOSink sink = file.openWrite(mode: FileMode.append);

    try {
      final List<String> tableNames = [
        'tb_location',
        'tb_gnss_status',
        'tb_gnss_measurement'
      ];

      // Iterar sobre as tabelas
      for (final tableName in tableNames) {
        // Adicionar nome da tabela como cabeçalho
        sink.writeln(tableName);

        // Query para selecionar todas as linhas da tabela
        final List<Map<String, dynamic>> dataList =
            await DatabaseProvider.rawQuery('SELECT * FROM $tableName');

        // Adicionar cabeçalhos à string CSV
        if (dataList.isNotEmpty) {
          sink.writeln(dataList.first.keys.join(','));
        }

        // Adicionar dados à string CSV
        for (final row in dataList) {
          sink.writeln(row.values.map((data) => '"$data"').join(','));
        }

        // Adicionar linha em branco entre tabelas
        sink.writeln();
      }
    } finally {
      await sink.close();
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        backgroundColor: const Color.fromARGB(255, 63, 169, 6),
        title: const Text('Página de Exportação'),
        leading: IconButton(
          icon: const Icon(Icons.arrow_back),
          onPressed: () {
            Navigator.pop(context);
          },
        ),
      ),
      body: Column(
        children: [
          DropdownButton<String>(
            value: _selectedOption,
            onChanged: (String? newValue) {
              if (newValue != null) {
                setState(() {
                  _selectedOption = newValue;
                  _currentPage = 0; // Reinicia a página ao mudar a opção
                  _fetchAllData();
                });
              }
            },
            items: <String>['Location', 'GNSS Status', 'GNSS Measurement']
                .map<DropdownMenuItem<String>>((String value) {
              return DropdownMenuItem<String>(
                value: value,
                child: Text(value),
              );
            }).toList(),
          ),
          if (!_dataLoaded)
            Expanded(
              child: Center(
                child: Text('Carregando dados...'),
              ),
            ),
          if (_dataLoaded && _currentLocationDataList.isEmpty)
            Expanded(
              child: Center(
                child: Text('Sem coletas realizadas.'),
              ),
            ),
          if (_dataLoaded && _currentLocationDataList.isNotEmpty)
            Expanded(
              child: PaginatedDataTable(
                columns: _currentLocationDataList.isNotEmpty
                    ? _currentLocationDataList.first.keys
                        .map((String key) => DataColumn(label: Text(key)))
                        .toList()
                    : [],
                rowsPerPage: _rowsPerPage,
                source: _DataTableDataSource(_currentLocationDataList),
                onPageChanged: (newPage) async {
                  _currentPage = newPage;
                  await _fetchDataForPage(_currentPage);
                },
                header: Text('$_currentPage - $_totalItems'),
              ),
            ),
          if (_dataLoaded && _currentLocationDataList.isNotEmpty)
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceEvenly,
              children: [
                ElevatedButton(
                  onPressed: _clearData,
                  child: Text('Limpar'),
                ),
                ElevatedButton(
                  onPressed: _exportData,
                  child: Text('Exportar'),
                ),
              ],
            ),
        ],
      ),
    );
  }
}

class _DataTableDataSource extends DataTableSource {
  final List<Map<String, dynamic>> _dataList;

  _DataTableDataSource(this._dataList);

  @override
  DataRow? getRow(int index) {
    if (index >= _dataList.length) {
      return null;
    }
    final data = _dataList[index];
    return DataRow(
        cells: data.keys
            .map((String key) => DataCell(Text(data[key].toString())))
            .toList());
  }

  @override
  bool get isRowCountApproximate => false;

  @override
  int get rowCount => _dataList.length;

  @override
  int get selectedRowCount => 0;
}
