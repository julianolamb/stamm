import 'dart:io';

import 'package:path/path.dart';
import 'package:path_provider/path_provider.dart';
import 'package:point_getter/models/gnss_measurement_model.dart';
import 'package:point_getter/models/gnss_status_model.dart';
import 'package:point_getter/models/location_model.dart';
import 'package:sqflite/sqflite.dart';

class DatabaseProvider {
  static late Database _database;

  static Future<Database> get database async {
    if (_database.isOpen) return _database;

    // Obter o diretório de documentos do aplicativo
    Directory appDocumentsDirectory = await getApplicationDocumentsDirectory();

    // Crie o diretório 'files' se ele não existir
    Directory filesDirectory = Directory('${appDocumentsDirectory.path}/files');
    if (!await filesDirectory.exists()) {
      await filesDirectory.create(recursive: true);
    }

    // Defina o caminho do arquivo do banco de dados dentro do diretório 'files'
    String path = join(filesDirectory.path, 'gnss_data.db');

    print('Caminho do banco de dados: $path');

    // Abra ou crie o banco de dados
    _database = await openDatabase(path, version: 1, onCreate: _createDatabase);

    return _database;
  }

  static Future<void> _initDatabase() async {
    try {
      // Obtém o diretório de documentos do aplicativo
      Directory documentsDirectory = await getApplicationDocumentsDirectory();
      // Define o caminho do arquivo do banco de dados dentro do diretório de documentos
      String path = join(documentsDirectory.path, 'gnss_data.db');

      print('Caminho do banco de dados: $path');

      // Abre ou cria o banco de dados
      _database =
          await openDatabase(path, version: 1, onCreate: _createDatabase);

      print('Banco de dados aberto com sucesso!');

      //insertTestLocationData();
    } catch (e) {
      print('Erro ao abrir ou criar o banco de dados: $e');
    }
  }

  static Future<void> _createDatabase(Database db, int version) async {
    try {
      print('Criando banco de dados...');

      // Cria as tabelas
      await _createLocationTable(db);
      await _createGnssStatusTable(db);
      await _createGnssMeasurementTable(db);

      print('Banco de dados criado com sucesso!');
    } catch (e) {
      print('Erro ao criar o banco de dados: $e');
    }
  }

  static Future<void> _createLocationTable(Database db) async {
    await db.execute('''
  CREATE TABLE IF NOT EXISTS tb_location(
      location_id INTEGER PRIMARY KEY AUTOINCREMENT,
      ponto_id INTEGER NOT NULL,
      rodada_id INTEGER NOT NULL,
      get_current_time TEXT,
      timestamp REAL,
      latitude REAL NOT NULL,
      longitude REAL NOT NULL,
      accuracy REAL,
      altitude REAL,
      altitude_accuracy REAL,
      floor INTEGER,
      speed REAL,
      speed_accuracy REAL,
      heading REAL,
      heading_accuracy REAL,
      is_mock INTEGER
  )
  ''');
  }

  /*static Future<void> _createLocationTable(Database db) async {
    await db.execute('''
    CREATE TABLE IF NOT EXISTS tb_location(
      location_id INTEGER PRIMARY KEY AUTOINCREMENT,
      ponto_id INTEGER NOT NULL,
      rodada_id INTEGER NOT NULL,
      get_current_time TEXT,
      get_accuracy REAL,
      get_altitude REAL,
      get_bearing REAL,
      get_bearing_accuracy_degrees REAL,
      get_elapsed_realtime_age_millis INTEGER,
      get_elapsed_realtime_millis INTEGER,
      get_elapsed_realtime_nanos INTEGER,
      get_elapsed_realtime_uncertainty_nanos REAL,
      get_extras TEXT,
      get_latitude REAL,
      get_longitude REAL,
      get_provider TEXT,
      get_speed REAL,
      get_speed_accuracy_meters_per_second REAL,
      get_time INTEGER,
      get_vertical_accuracy_meters REAL,
      has_accuracy INTEGER,
      has_altitude INTEGER,
      has_bearing INTEGER,
      has_bearing_accuracy INTEGER,
      has_elapsed_realtime_uncertainty_nanos INTEGER,
      has_speed INTEGER,
      has_speed_accuracy INTEGER,
      has_vertical_accuracy INTEGER,
      is_complete INTEGER,
      is_mock INTEGER
    )
  ''');
  }*/

  static Future<void> _createGnssStatusTable(Database db) async {
    await db.execute('''
    CREATE TABLE IF NOT EXISTS tb_gnss_status(
      gnss_status_id INTEGER PRIMARY KEY,
      ponto_id INTEGER NOT NULL,
      rodada_id INTEGER NOT NULL,
      get_current_time TEXT,
      get_azimuth_degrees REAL,
      get_baseband_cn0_db_hz REAL,
      get_carrier_frequency_hz REAL,
      get_cn0_db_hz REAL,
      get_constellation_type INTEGER,
      get_elevation_degrees REAL,
      get_satellite_count INTEGER,
      get_svid INTEGER,
      has_almanac_data INTEGER,
      has_baseband_cn0_db_hz INTEGER,
      has_carrier_frequency_hz INTEGER,
      has_ephemeris_data INTEGER,
      used_in_fix INTEGER
    )
  ''');
  }

  static Future<void> _createGnssMeasurementTable(Database db) async {
    await db.execute('''
    CREATE TABLE IF NOT EXISTS tb_gnss_measurement(
      gnss_measurement_id INTEGER PRIMARY KEY,
      ponto_id INTEGER NOT NULL,
      rodada_id INTEGER NOT NULL,
      get_current_time TEXT,
      get_accumulated_delta_range_meters REAL,
      get_accumulated_delta_range_state INTEGER,
      get_accumulated_delta_range_uncertainty_meters REAL,
      get_baseband_cn0_db_hz REAL,
      get_carrier_frequency_hz REAL,
      get_cn0_db_hz REAL,
      get_code_type TEXT,
      get_constellation_type INTEGER,
      get_full_inter_signal_bias_nanos REAL,
      get_full_inter_signal_bias_uncertainty_nanos REAL,
      get_multipath_indicator INTEGER,
      get_pseudorange_rate_meters_per_second REAL,
      get_pseudorange_rate_uncertainty_meters_per_second REAL,
      get_received_sv_time_nanos INTEGER,
      get_received_sv_time_uncertainty_nanos INTEGER,
      get_satellite_inter_signal_bias_nanos REAL,
      get_satellite_inter_signal_bias_uncertainty_nanos REAL,
      get_snr_in_db REAL,
      get_state INTEGER,
      get_svid INTEGER,
      get_time_offset_nanos REAL,
      has_baseband_cn0_db_hz INTEGER,
      has_carrier_frequency_hz INTEGER,
      has_code_type INTEGER,
      has_full_inter_signal_bias_nanos INTEGER,
      has_full_inter_signal_bias_uncertainty_nanos INTEGER,
      has_satellite_inter_signal_bias_nanos INTEGER,
      has_satellite_inter_signal_bias_uncertainty_nanos INTEGER,
      has_snr_in_db INTEGER
    )
  ''');
  }

  static Future<void> transaction(
      Future<void> Function(Transaction txn) action) async {
    final db = await database;
    await db.transaction(action);
  }

  static Future<void> insertLocationData(LocationDataModel data) async {
    final db = await database;
    await db.insert(
      'tb_location',
      data.toMap(), // Converte o objeto para um mapa para inserção
    );
  }

  static Future<void> insertGnssStatusData(GnssStatusModel data) async {
    final db = await database;
    await db.insert(
      'tb_gnss_status',
      data.toMap(), // Converte o objeto para um mapa para inserção
    );
  }

  static Future<void> insertGnssMeasurementData(
      GnssMeasurementModel data) async {
    final db = await database;
    await db.insert(
      'tb_gnss_measurement',
      data.toMap(), // Converte o objeto para um mapa para inserção
    );
  }

  static Future<List<Map<String, dynamic>>> rawQuery(String query) async {
    final Database db = await database;
    return await db.rawQuery(query);
  }

  static Future<int> rawDelete(String query) async {
    final Database db = await database;
    return await db.rawDelete(query);
  }

  static Future<void> initDatabase() async {
    // Inicialize o banco de dados
    await _initDatabase();
  }
}
