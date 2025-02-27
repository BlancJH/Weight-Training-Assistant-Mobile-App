import 'package:flutter/material.dart';
import '../models/exercise.dart';
import '../services/exercise_service.dart';

class ExerciseSearchPopup extends StatefulWidget {
  final Function(Exercise) onExerciseSelected;

  const ExerciseSearchPopup({
    Key? key,
    required this.onExerciseSelected,
  }) : super(key: key);

  @override
  _ExerciseSearchPopupState createState() => _ExerciseSearchPopupState();
}

class _ExerciseSearchPopupState extends State<ExerciseSearchPopup> {
  String searchQuery = '';
  String? selectedPrimaryMuscle;
  String? selectedCategory;

  List<Exercise> searchResults = [];
  bool isLoading = false;
  String? errorMessage;

  // Create an instance of your API service
  final ExerciseService exerciseService = ExerciseService();

  // Call the API when user presses search.
  void _performSearch() async {
    setState(() {
      isLoading = true;
      errorMessage = null;
      searchResults = [];
    });

    try {
      // Call the API with name and primaryMuscle.
      // Note: The backend controller supports only 'name' and 'primaryMuscle'.
      List<Exercise> results = await exerciseService.searchExercises(
        name: searchQuery,
        primaryMuscle: selectedPrimaryMuscle,
      );

      // Optionally, further filter by category locally if needed.
      if (selectedCategory != null && selectedCategory!.isNotEmpty) {
        results = results
            .where((exercise) => exercise.category == selectedCategory)
            .toList();
      }

      setState(() {
        searchResults = results;
      });
    } catch (error) {
      setState(() {
        errorMessage = error.toString();
      });
    } finally {
      setState(() {
        isLoading = false;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return SafeArea(
      child: Padding(
        // Adjust padding as needed
        padding: const EdgeInsets.all(16.0),
        child: Column(
          mainAxisSize: MainAxisSize.min, // Size to its content
          children: [
            // Search Bar
            TextField(
              decoration: InputDecoration(
                hintText: 'Search exercise...',
                prefixIcon: Icon(Icons.search),
                border: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(8.0),
                ),
              ),
              onChanged: (value) {
                searchQuery = value;
              },
            ),
            const SizedBox(height: 16.0),
            // Dropdowns for filtering
            Row(
              children: [
                Expanded(
                  child: DropdownButtonFormField<String>(
                    decoration: InputDecoration(
                      labelText: 'Primary Muscle',
                      border: OutlineInputBorder(
                        borderRadius: BorderRadius.circular(8.0),
                      ),
                    ),
                    value: selectedPrimaryMuscle,
                    isExpanded: true,
                    items: <String>['Chest', 'Back', 'Legs', 'Arms', 'Shoulders']
                        .map((muscle) => DropdownMenuItem(
                              value: muscle,
                              child: Text(muscle),
                            ))
                        .toList(),
                    onChanged: (value) {
                      setState(() {
                        selectedPrimaryMuscle = value;
                      });
                    },
                  ),
                ),
                const SizedBox(width: 8.0),
                Expanded(
                  child: DropdownButtonFormField<String>(
                    decoration: InputDecoration(
                      labelText: 'Category',
                      border: OutlineInputBorder(
                        borderRadius: BorderRadius.circular(8.0),
                      ),
                    ),
                    value: selectedCategory,
                    isExpanded: true,
                    items: <String>['Strength', 'Cardio', 'Flexibility']
                        .map((cat) => DropdownMenuItem(
                              value: cat,
                              child: Text(cat),
                            ))
                        .toList(),
                    onChanged: (value) {
                      setState(() {
                        selectedCategory = value;
                      });
                    },
                  ),
                ),
              ],
            ),
            const SizedBox(height: 16.0),
            // Search button
            ElevatedButton(
              onPressed: _performSearch,
              child: Text('Search'),
            ),
            const SizedBox(height: 16.0),
            // Display loading indicator or error message if applicable
            if (isLoading) CircularProgressIndicator(),
            if (errorMessage != null)
              Text(
                errorMessage!,
                style: TextStyle(color: Colors.red),
              ),
            // List of search results
            Expanded(
              child: searchResults.isNotEmpty
                  ? ListView.builder(
                      itemCount: searchResults.length,
                      itemBuilder: (context, index) {
                        final exercise = searchResults[index];
                        return Draggable<Exercise>(
                          data: exercise,
                          feedback: Material(
                            color: Colors.transparent,
                            child: ListTile(
                              leading: Image.network(
                                exercise.gifUrl,
                                width: 50,
                                height: 50,
                                fit: BoxFit.cover,
                                errorBuilder: (context, error, stackTrace) =>
                                    Icon(Icons.broken_image),
                              ),
                              title: Text(exercise.name),
                              subtitle: Text(
                                  '${exercise.primaryMuscle} • ${exercise.category}'),
                            ),
                          ),
                          childWhenDragging: Opacity(
                            opacity: 0.5,
                            child: ListTile(
                              leading: Image.network(
                                exercise.gifUrl,
                                width: 50,
                                height: 50,
                                fit: BoxFit.cover,
                                errorBuilder: (context, error, stackTrace) =>
                                    Icon(Icons.broken_image),
                              ),
                              title: Text(exercise.name),
                              subtitle: Text(
                                  '${exercise.primaryMuscle} • ${exercise.category}'),
                            ),
                          ),
                          child: ListTile(
                            leading: Image.network(
                              exercise.gifUrl,
                              width: 50,
                              height: 50,
                              fit: BoxFit.cover,
                              errorBuilder: (context, error, stackTrace) =>
                                  Icon(Icons.broken_image),
                            ),
                            title: Text(exercise.name),
                            subtitle: Text(
                                '${exercise.primaryMuscle} • ${exercise.category}'),
                            onTap: () {
                              widget.onExerciseSelected(exercise);
                            },
                          ),
                        );
                      },
                    )
                  : Center(child: Text('No results found')),
            ),
          ],
        ),
      ),
    );
  }
}
