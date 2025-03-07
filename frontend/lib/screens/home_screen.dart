import 'package:flutter/material.dart';
import 'package:frontend_1/screens/calendar_screen.dart';
import 'package:frontend_1/screens/profile_screen.dart';
import 'package:frontend_1/screens/sphere_screen.dart';
import 'package:frontend_1/services/auth_service.dart';
import 'package:frontend_1/widgets/popup_menu.dart';
import 'package:frontend_1/widgets/profile_avatar.dart';

class HomeScreen extends StatefulWidget {
  @override
  _HomeScreenState createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  final AuthService _authService = AuthService();
  String? username;
  String? profileUrl;
  int _currentIndex = 0; // 0 = Calendar, 1 = Sphere

  @override
  void initState() {
    super.initState();
    _fetchUserData();
  }

  Future<void> _fetchUserData() async {
    try {
      final userData = await _authService.decodeToken();
      setState(() {
        username = userData?['username'] ?? 'Guest';
        profileUrl = userData?['profileUrl'];
      });
    } catch (e) {
      print('Error fetching user data: $e');
      setState(() {
        username = 'Guest';
        profileUrl = null;
      });
    }
  }

  void _handleMenuSelection(MenuOptions option) async {
    switch (option) {
      case MenuOptions.profile:
        Navigator.push(
          context,
          MaterialPageRoute(
            builder: (context) => ProfileScreen(
              profileImageUrl: profileUrl ?? '',
              username: username ?? 'Guest',
            ),
          ),
        );
        break;
      case MenuOptions.settings:
        print('Settings tapped!');
        break;
      case MenuOptions.logout:
        await _authService.logoutUser();
        Navigator.pushReplacementNamed(context, '/login');
        break;
    }
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    // Create the two pages and pass common user data.
    final List<Widget> pages = [
      CalendarPage(username: username, profileUrl: profileUrl),
      SpherePage(username: username, profileUrl: profileUrl),
    ];

    return Scaffold(
      appBar: AppBar(
        centerTitle: false,
        backgroundColor: theme.colorScheme.surface,
        title: Text('Gymtinued'),
        actions: [
          PopupMenuWidget(
            trigger: ProfileAvatar(
              username: username ?? 'Loading...',
              imageUrl: profileUrl ?? '',
            ),
            onSelected: _handleMenuSelection,
          ),
        ],
      ),
      body: IndexedStack(
        index: _currentIndex,
        children: pages,
      ),
      bottomNavigationBar: BottomNavigationBar(
        currentIndex: _currentIndex,
        backgroundColor: Colors.black,
        selectedItemColor: Colors.cyanAccent,
        unselectedItemColor: Colors.grey,
        onTap: (index) => setState(() => _currentIndex = index),
        items: const [
          BottomNavigationBarItem(
            icon: Icon(Icons.calendar_today),
            label: 'Calendar',
          ),
          BottomNavigationBarItem(
            icon: Icon(Icons.public),
            label: 'Sphere',
          ),
        ],
      ),
    );
  }
}