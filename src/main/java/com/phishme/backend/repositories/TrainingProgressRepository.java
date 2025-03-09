package com.phishme.backend.repositories;

import com.phishme.backend.entities.Training;
import com.phishme.backend.entities.TrainingProgress;
import com.phishme.backend.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TrainingProgressRepository extends JpaRepository<TrainingProgress, Long> {
    List<TrainingProgress> findByUserAndTraining(Users user, Training training);

    List<TrainingProgress> findByUser(Users user);

    Optional<TrainingProgress> findByUserAndTrainingAndScenarioId(Users user, Training training, String scenarioId);
}
