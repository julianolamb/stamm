import 'package:flutter/material.dart';

class DefaultDrawer extends StatelessWidget {
  const DefaultDrawer({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Drawer(
      child: ListView(
        padding: EdgeInsets.zero,
        children: <Widget>[
          const DrawerHeader(
            decoration: BoxDecoration(
              color: Colors.blue,
            ),
            child: Text('Menu'),
          ),
          ListTile(
            title: const Text('Opção 1'),
            onTap: () {},
          ),
          ListTile(
            title: const Text('Opção 2'),
            onTap: () {},
          ),
        ],
      ),
    );
  }
}
