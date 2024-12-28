package com.blancJH.weight_assistant_mobile_app_backend.controller;

import com.blancJH.weight_assistant_mobile_app_backend.model.Exercise;
import com.blancJH.weight_assistant_mobile_app_backend.service.ExerciseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExerciseControllerTest {

    @Mock
    private ExerciseService exerciseService;

    @InjectMocks
    private ExerciseController exerciseController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateExercise() {
        Exercise exercise = new Exercise();
        exercise.setExerciseName("Push-Up");
        exercise.setExerciseCategory("Strength");
        exercise.setMuscles(List.of("Chest", "Triceps"));
        exercise.setExerciseGifUrl("https://example.com/pushup.gif");

        when(exerciseService.createExercise(exercise)).thenReturn(exercise);

        ResponseEntity<Exercise> response = exerciseController.createExercise(exercise);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Push-Up", response.getBody().getExerciseName());
        verify(exerciseService, times(1)).createExercise(exercise);
    }

    @Test
    void testGetAllExercises() {
        Exercise exercise1 = new Exercise();
        exercise1.setExerciseName("Push-Up");
        exercise1.setExerciseCategory("Strength");
        exercise1.setMuscles(List.of("Chest", "Triceps"));

        Exercise exercise2 = new Exercise();
        exercise2.setExerciseName("Squat");
        exercise2.setExerciseCategory("Strength");
        exercise2.setMuscles(List.of("Legs", "Glutes"));

        when(exerciseService.getAllExercises()).thenReturn(Arrays.asList(exercise1, exercise2));

        ResponseEntity<List<Exercise>> response = exerciseController.getAllExercises();

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
        verify(exerciseService, times(1)).getAllExercises();
    }

    @Test
    void testGetExerciseById() {
        Exercise exercise = new Exercise();
        exercise.setExerciseName("Push-Up");
        exercise.setExerciseCategory("Strength");
        exercise.setMuscles(List.of("Chest", "Triceps"));

        when(exerciseService.getExerciseById(1L)).thenReturn(exercise);

        ResponseEntity<Exercise> response = exerciseController.getExerciseById(1L);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Push-Up", response.getBody().getExerciseName());
        verify(exerciseService, times(1)).getExerciseById(1L);
    }

    @Test
    void testUpdateExercise() {
        Exercise updatedExercise = new Exercise();
        updatedExercise.setExerciseName("Pull-Up");
        updatedExercise.setExerciseCategory("Strength");
        updatedExercise.setMuscles(List.of("Back", "Biceps"));

        when(exerciseService.updateExercise(eq(1L), any(Exercise.class))).thenReturn(updatedExercise);

        ResponseEntity<Exercise> response = exerciseController.updateExercise(1L, updatedExercise);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Pull-Up", response.getBody().getExerciseName());
        verify(exerciseService, times(1)).updateExercise(eq(1L), any(Exercise.class));
    }

    @Test
    void testDeleteExercise() {
        doNothing().when(exerciseService).deleteExercise(1L);

        ResponseEntity<Void> response = exerciseController.deleteExercise(1L);

        assertNotNull(response);
        assertEquals(204, response.getStatusCodeValue());
        verify(exerciseService, times(1)).deleteExercise(1L);
    }

    @Test
    void testCreateExercisesInBulk() {
        Exercise exercise1 = new Exercise();
        exercise1.setExerciseName("Push-Up");
        exercise1.setExerciseCategory("Strength");
        exercise1.setMuscles(List.of("Chest", "Triceps"));

        Exercise exercise2 = new Exercise();
        exercise2.setExerciseName("Squat");
        exercise2.setExerciseCategory("Strength");
        exercise2.setMuscles(List.of("Legs", "Glutes"));

        List<Exercise> exercises = List.of(exercise1, exercise2);

        when(exerciseService.createExercises(exercises)).thenReturn(exercises);

        ResponseEntity<List<Exercise>> response = exerciseController.createExercises(exercises);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
        verify(exerciseService, times(1)).createExercises(exercises);
    }
}
