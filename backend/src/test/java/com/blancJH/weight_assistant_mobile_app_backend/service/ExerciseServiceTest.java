package com.blancJH.weight_assistant_mobile_app_backend.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.blancJH.weight_assistant_mobile_app_backend.model.Exercise;
import com.blancJH.weight_assistant_mobile_app_backend.model.ExerciseCategory;
import com.blancJH.weight_assistant_mobile_app_backend.repository.ExerciseRepository;

class ExerciseServiceTest {

    @Mock
    private ExerciseRepository exerciseRepository;

    @InjectMocks
    private ExerciseService exerciseService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateExercise() {
        Exercise exercise = new Exercise();
        exercise.setExerciseName("Push-Up");
        exercise.setExerciseCategory(ExerciseCategory.BODYWEIGHT);
        exercise.setPrimaryMuscle("Chest");
        exercise.setSecondaryMuscle("Triceps");
        exercise.setExerciseGifUrl("https://example.com/pushup.gif");

        when(exerciseRepository.save(exercise)).thenReturn(exercise);

        Exercise createdExercise = exerciseService.createExercise(exercise);

        assertNotNull(createdExercise);
        assertEquals("Push-Up", createdExercise.getExerciseName());
        verify(exerciseRepository, times(1)).save(exercise);
    }

    @Test
    void testGetAllExercises() {
        Exercise exercise1 = new Exercise();
        exercise1.setExerciseName("Push-Up");
        exercise1.setExerciseCategory(ExerciseCategory.BODYWEIGHT);
        exercise1.setPrimaryMuscle("Chest");
        exercise1.setSecondaryMuscle("Triceps");

        Exercise exercise2 = new Exercise();
        exercise2.setExerciseName("Squat");
        exercise2.setExerciseCategory(ExerciseCategory.BARBELL);
        exercise2.setPrimaryMuscle("Legs");
        exercise2.setSecondaryMuscle("Glutes");

        when(exerciseRepository.findAll()).thenReturn(Arrays.asList(exercise1, exercise2));

        List<Exercise> exercises = exerciseService.getAllExercises();

        assertNotNull(exercises);
        assertEquals(2, exercises.size());
        verify(exerciseRepository, times(1)).findAll();
    }

    @Test
    void testGetExerciseById() {
        Exercise exercise = new Exercise();
        exercise.setExerciseName("Push-Up");
        exercise.setExerciseCategory(ExerciseCategory.BODYWEIGHT);
        exercise.setPrimaryMuscle("Chest");
        exercise.setSecondaryMuscle("Triceps");

        when(exerciseRepository.findById(1L)).thenReturn(Optional.of(exercise));

        Exercise foundExercise = exerciseService.getExerciseById(1L);

        assertNotNull(foundExercise);
        assertEquals("Push-Up", foundExercise.getExerciseName());
        verify(exerciseRepository, times(1)).findById(1L);
    }

    @Test
    void testDeleteExercise() {
        doNothing().when(exerciseRepository).deleteById(1L);

        exerciseService.deleteExercise(1L);

        verify(exerciseRepository, times(1)).deleteById(1L);
    }
}
