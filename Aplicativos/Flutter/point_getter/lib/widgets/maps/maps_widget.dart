import 'dart:async';
import 'package:flutter/material.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';
import 'package:location/location.dart';

class MapsWidget extends StatefulWidget {
  final Set<Marker>? markers;
  final bool isPaused;

  const MapsWidget({Key? key, this.markers, required this.isPaused})
      : super(key: key);

  @override
  _MapsWidgetState createState() => _MapsWidgetState();
}

class _MapsWidgetState extends State<MapsWidget> {
  late GoogleMapController mapController;
  late BitmapDescriptor myLocationIcon;
  LocationData? currentLocation;
  late StreamSubscription<LocationData> locationSubscription;
  late bool zoomedIn;
  late MapType _currentMapType;
  bool _loading = true;

  @override
  void initState() {
    super.initState();
    zoomedIn = false;
    _setupMyLocationIcon();
    _getCurrentLocation();
    _currentMapType = MapType.normal;
    print("initState: isPaused = ${widget.isPaused}"); // Debug output
  }

  @override
  void didUpdateWidget(MapsWidget oldWidget) {
    super.didUpdateWidget(oldWidget);
    if (oldWidget.isPaused != widget.isPaused) {
      print(
          "didUpdateWidget: isPaused changed from ${oldWidget.isPaused} to ${widget.isPaused}");
      _getCurrentLocation(); // Re-check location updates based on the pause state
    }
  }

  Future<void> _setupMyLocationIcon() async {
    myLocationIcon = await BitmapDescriptor.fromAssetImage(
      const ImageConfiguration(devicePixelRatio: 0.05),
      'lib/src/images/gps-icon.png',
    );
  }

  Future<void> _getCurrentLocation() async {
    final location = Location();

    bool serviceEnabled = await location.serviceEnabled();
    if (!serviceEnabled) {
      serviceEnabled = await location.requestService();
      if (!serviceEnabled) return;
    }

    PermissionStatus permissionStatus = await location.hasPermission();
    if (permissionStatus == PermissionStatus.denied) {
      permissionStatus = await location.requestPermission();
      if (permissionStatus != PermissionStatus.granted) return;
    }

    await location.changeSettings(accuracy: LocationAccuracy.high);
    print("_getCurrentLocation: isPaused = ${widget.isPaused}"); // Debug output

    if (!widget.isPaused) {
      LocationData? locationData = await location.getLocation();
      setState(() {
        currentLocation = locationData;
        _loading = false;
      });

      locationSubscription =
          location.onLocationChanged.listen((LocationData newLocation) {
        setState(() => currentLocation = newLocation);
      });
    } else {
      locationSubscription.cancel();
      setState(() {
        currentLocation = null;
        _loading = false;
      });
    }
  }

  @override
  void dispose() {
    locationSubscription.cancel();
    super.dispose();
  }

  void _toggleZoom() {
    double targetZoom = zoomedIn ? 17.0 : 25.0;
    if (mapController != null) {
      mapController.animateCamera(
        CameraUpdate.newLatLngZoom(
          LatLng(
            currentLocation!.latitude!,
            currentLocation!.longitude!,
          ),
          targetZoom,
        ),
      );
      setState(() {
        zoomedIn = !zoomedIn;
      });
    }
  }

  void _onMapTypeButtonPressed() {
    showModalBottomSheet(
      context: context,
      builder: (builder) {
        return Container(
          child: ListView(
            shrinkWrap: true,
            children: <Widget>[
              ListTile(
                title: const Text('Normal'),
                onTap: () {
                  _changeMapType(MapType.normal);
                  Navigator.pop(context);
                },
              ),
              ListTile(
                title: const Text('Satélite'),
                onTap: () {
                  _changeMapType(MapType.satellite);
                  Navigator.pop(context);
                },
              ),
              ListTile(
                title: const Text('Terreno'),
                onTap: () {
                  _changeMapType(MapType.terrain);
                  Navigator.pop(context);
                },
              ),
              ListTile(
                title: const Text('Híbrido'),
                onTap: () {
                  _changeMapType(MapType.hybrid);
                  Navigator.pop(context);
                },
              ),
            ],
          ),
        );
      },
    );
  }

  void _changeMapType(MapType type) {
    setState(() {
      _currentMapType = type;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Stack(
      children: [
        if (!_loading && currentLocation != null)
          Container(
            height: MediaQuery.of(context).size.height * 0.5,
            child: GoogleMap(
              onMapCreated: (controller) {
                mapController = controller;
              },
              initialCameraPosition: CameraPosition(
                target: LatLng(
                  currentLocation!.latitude!,
                  currentLocation!.longitude!,
                ),
                zoom: 14.0,
              ),
              mapType: _currentMapType,
              markers: {
                if (currentLocation != null && myLocationIcon != null)
                  Marker(
                    markerId: const MarkerId('myLocation'),
                    position: LatLng(
                      currentLocation!.latitude!,
                      currentLocation!.longitude!,
                    ),
                    icon: myLocationIcon,
                  ),
                if (widget.markers != null) ...widget.markers!,
              },
            ),
          ),
        Positioned(
          top: 5.0,
          right: 6.0,
          width: 30.0,
          height: 30,
          child: FloatingActionButton(
            onPressed: _onMapTypeButtonPressed,
            child: const Icon(Icons.map),
          ),
        ),
        Positioned(
          bottom: 5.0,
          left: 6.0,
          child: FloatingActionButton(
            onPressed: _toggleZoom,
            child: const Icon(Icons.location_searching),
          ),
        ),
      ],
    );
  }
}
