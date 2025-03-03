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

  // Instance of your API service.
  final ExerciseService exerciseService = ExerciseService();

  // Call the API when user presses the Search button.
  void _performSearch() async {
    setState(() {
      isLoading = true;
      errorMessage = null;
      searchResults = [];
    });

    try {
      // Call the API with 'name' and 'primaryMuscle'
      List<Exercise> results = await exerciseService.searchExercises(
        name: searchQuery,
        primaryMuscle: selectedPrimaryMuscle,
      );

      // Optionally filter locally by category.
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

  // Build a simple ListTile for an exercise.
  Widget _buildExerciseTile(Exercise exercise) {
    return ListTile(
      leading: Image.network(
        exercise.gifUrl,
        width: 50,
        height: 50,
        fit: BoxFit.cover,
        errorBuilder: (context, error, stackTrace) =>
            const Icon(Icons.broken_image),
      ),
      title: Text(
        exercise.exerciseName,
        style: const TextStyle(color: Colors.black, fontSize: 16.0),
      ),
      onTap: () {
        // Call the callback to add the exercise to the plan.
        widget.onExerciseSelected(exercise);
        // Close the popup.
        Navigator.of(context, rootNavigator: true).pop();
      },
    );
  }

  @override
  Widget build(BuildContext context) {
    return SafeArea(
      child: Padding(
        // Adjust padding as needed.
        padding: const EdgeInsets.all(16.0),
        child: Column(
          mainAxisSize: MainAxisSize.min, // Size to its content.
          children: [
            // Search Bar
            TextField(
              decoration: InputDecoration(
                hintText: 'Search exercise...',
                prefixIcon: const Icon(Icons.search),
                border: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(8.0),
                ),
              ),
              onChanged: (value) {
                searchQuery = value;
              },
            ),
            const SizedBox(height: 16.0),
            // Row for dropdown filters
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
              child: const Text('Search'),
            ),
            const SizedBox(height: 16.0),
            // Show loading indicator or error message if applicable.
            if (isLoading) const CircularProgressIndicator(),
            if (errorMessage != null)
              Text(
                errorMessage!,
                style: const TextStyle(color: Colors.red),
              ),
            // Scrollable list of search results.
            Expanded(
              child: searchResults.isNotEmpty
                  ? ListView.builder(
                      itemCount: searchResults.length,
                      itemBuilder: (context, index) {
                        final exercise = searchResults[index];
                        return _buildExerciseTile(exercise);
                      },
                    )
                  : const Center(child: Text('No results found')),
            ),
          ],
        ),
      ),
    );
  }
}
