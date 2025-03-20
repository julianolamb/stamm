import 'package:flutter/material.dart';
import 'package:point_getter/screens/gnssdata_screen.dart';
import 'package:point_getter/services/provider/database_provider.dart';
import 'package:point_getter/services/provider/points_provider.dart';
import 'screens/collect_screen.dart';
import 'screens/management_screen.dart';
import 'screens/export_screen.dart';
import 'widgets/drawer/default_drawer.dart';
import 'widgets/main/option_widget.dart';
import 'package:provider/provider.dart';

void main() async {
  WidgetsFlutterBinding
      .ensureInitialized(); // Garante que os plugins estejam inicializados

  // Inicializa o banco de dados
  await DatabaseProvider.initDatabase();

  // Roda o aplicativo
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return MultiProvider(
        providers: [
          ChangeNotifierProvider(create: (_) => PointsProvider()),
        ],
        child: MaterialApp(
          debugShowCheckedModeBanner: false,
          title: 'PointGetter',
          theme: ThemeData(
            colorScheme: ColorScheme.fromSeed(seedColor: Colors.deepPurple),
            useMaterial3: true,
          ),
          routes: {
            '/': (context) => const MyHomePage(title: 'PointGetter'),
            '/CollectScreen': (context) => CollectScreen(),
            '/ManagementScreen': (context) => ManagementScreen(),
            '/ExportScreen': (context) => ExportScreen(),
            '/GnssDataScreen': (context) => GnssDataScreen(),
          },
        ));
  }
}

class MyHomePage extends StatefulWidget {
  const MyHomePage({Key? key, required this.title}) : super(key: key);
  final String title;

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        backgroundColor: const Color.fromARGB(255, 63, 169, 6),
        title: Text(widget.title),
      ),
      drawer: const DefaultDrawer(),
      body: Container(
        child: Stack(
          children: [
            Image.asset(
              'lib/src/images/main-background.png',
              fit: BoxFit.cover,
              width: double.infinity,
              height: double.infinity,
            ),
            Container(
              color: Colors.white.withOpacity(0.2),
              width: double.infinity,
              height: double.infinity,
            ),
            Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.start,
                crossAxisAlignment: CrossAxisAlignment.center,
                children: <Widget>[
                  const SizedBox(height: 50), // Reduzido o espaço
                  const Text(
                    'Menu Principal',
                    style: TextStyle(
                      fontSize: 40, // Aumenta o tamanho da fonte
                      fontWeight: FontWeight.bold,
                      color: Colors.black,
                      decoration:
                          TextDecoration.underline, // Adiciona sublinhado
                    ),
                  ),
                  const SizedBox(height: 30), // Aumentado o espaço
                  OptionWidget(
                    title: 'GERENCIAR PONTOS', // Renomeia a opção 2
                    color: Colors.lightGreen,
                    iconImagePath: 'lib/src/images/c_gerenciar-ponto.png',
                    onTap: () {
                      Navigator.pushNamed(context, '/ManagementScreen');
                    },
                  ),
                  const SizedBox(height: 40), // Aumentado o espaço
                  OptionWidget(
                    title: 'COLETA', // Renomeia a opção 1
                    color: Colors.lightGreen,
                    iconImagePath: 'lib/src/images/c_gerenciar-ponto.png',
                    onTap: () {
                      Navigator.pushNamed(context, '/CollectScreen');
                    },
                  ),
                  const SizedBox(height: 40), // Aumentado o espaço
                  OptionWidget(
                    title: 'DADOS', // Renomeia a opção 3
                    color: Colors.lightGreen,
                    iconImagePath: 'lib/src/images/c_gerenciar-ponto.png',
                    onTap: () {
                      Navigator.pushNamed(context, '/ExportScreen');
                    },
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }
}
