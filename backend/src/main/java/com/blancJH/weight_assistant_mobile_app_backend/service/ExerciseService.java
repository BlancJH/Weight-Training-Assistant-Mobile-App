package com.blancJH.weight_assistant_mobile_app_backend.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.blancJH.weight_assistant_mobile_app_backend.model.Exercise;
import com.blancJH.weight_assistant_mobile_app_backend.model.WorkoutPlanExercise;
import com.blancJH.weight_assistant_mobile_app_backend.repository.ExerciseRepository;
import com.blancJH.weight_assistant_mobile_app_backend.repository.WorkoutPlanExerciseRepository;

@Service
public class ExerciseService {

    private final ExerciseRepository exerciseRepository;
    private final WorkoutPlanExerciseRepository workoutPlanExerciseRepository;

    private static final Logger logger = LoggerFactory.getLogger(ExerciseService.class);

    @Autowired
    public ExerciseService(ExerciseRepository exerciseRepository, WorkoutPlanExerciseRepository workoutPlanExerciseRepository) {
        this.exerciseRepository = exerciseRepository;
        this.workoutPlanExerciseRepository = workoutPlanExerciseRepository;
    }

    public Exercise createExercise(Exercise exercise) {
        return exerciseRepository.save(exercise);
    }

    public List<Exercise> getAllExercises() {
        return exerciseRepository.findAll();
    }

    public Exercise getExerciseById(Long id) {
        return exerciseRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Exercise not found for id: " + id);
                    return new IllegalArgumentException("Exercise not found with id: " + id);
                });
    }

    public void deleteExercise(Long id) {
        exerciseRepository.deleteById(id);
    }

    public List<Exercise> createExercises(List<Exercise> exercises) {
        return exerciseRepository.saveAll(exercises);
    }

    public Exercise updateExercise(Long id, Exercise updatedExercise) {
        Exercise exercise = getExerciseById(id);
        exercise.setExerciseName(updatedExercise.getExerciseName());
        exercise.setExerciseCategory(updatedExercise.getExerciseCategory());
        exercise.setPrimaryMuscle(updatedExercise.getPrimaryMuscle());
        exercise.setSecondaryMuscle(updatedExercise.getSecondaryMuscle());
        exercise.setExerciseGifUrl(updatedExercise.getExerciseGifUrl());
        return exerciseRepository.save(exercise);
    }

    public List<WorkoutPlanExercise> getExercisesByWorkoutPlanId(Long workoutPlanId) {
        return workoutPlanExerciseRepository.findByWorkoutPlan_Id(workoutPlanId);
    }

    public List<Exercise> searchExercisesByName(String searchTerm) {
        return exerciseRepository.findByExerciseNameContainingIgnoreCase(searchTerm);
    }
    
    // Search by primary muscle (exact match, as a string)
    public List<Exercise> searchExercisesByPrimaryMuscle(String primaryMuscle) {
        return exerciseRepository.findByPrimaryMuscle(primaryMuscle);
    }

    public List<Exercise> searchExercises(String searchTerm, String primaryMuscle) {
        return exerciseRepository.findByExerciseNameContainingIgnoreCaseAndPrimaryMuscle(searchTerm, primaryMuscle);
    }

}
