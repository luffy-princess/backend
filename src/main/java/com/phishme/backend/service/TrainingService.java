package com.phishme.backend.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.phishme.backend.entities.Training;
import com.phishme.backend.entities.TrainingProgress;
import com.phishme.backend.entities.Users;
import com.phishme.backend.repositories.TrainingProgressRepository;
import com.phishme.backend.repositories.TrainingRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TrainingService {
    private final TrainingRepository trainingRepository;
    private final TrainingProgressRepository trainingProgressRepository;

    public List<Training> getAllTrainings() {
        return trainingRepository.findAll();
    }

    public Training getTrainingById(Long id) {
        return trainingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Training not found"));
    }

    @Transactional
    public void saveProgress(Long trainingId, String scenarioId, Users user, int score) {
        Training training = trainingRepository.findById(trainingId)
                .orElseThrow(() -> new RuntimeException("Training not found"));

        // 기존 진행 상황 찾기
        TrainingProgress progress = trainingProgressRepository
                .findByUserAndTrainingAndScenarioId(user, training, scenarioId)
                .orElseGet(() -> {
                    // 없으면 새로 생성
                    TrainingProgress newProgress = new TrainingProgress();
                    newProgress.setTraining(training);
                    newProgress.setScenarioId(scenarioId);
                    newProgress.setUser(user);
                    return newProgress;
                });

        // 점수와 완료 시간 업데이트
        progress.setScore(score);
        progress.setCompletedAt(LocalDateTime.now());

        trainingProgressRepository.save(progress);
    }

    public List<TrainingProgress> getUserProgress(Users user, Training training) {
        return trainingProgressRepository.findByUserAndTraining(user, training);
    }

    public boolean isTrainingCompleted(Training training, Users user) {
        try {
            // trainingData에서 시나리오 목록 가져오기
            Map<String, Object> trainingData = new ObjectMapper().readValue(training.getTrainingData(), Map.class);
            List<Map<String, Object>> scenarios = (List<Map<String, Object>>) trainingData.get("scenarios");

            if (scenarios == null || scenarios.isEmpty()) {
                return false;
            }

            // 사용자의 진행상황 가져오기
            List<TrainingProgress> progress = trainingProgressRepository.findByUserAndTraining(user, training);
            Set<String> completedScenarioIds = progress.stream()
                    .map(TrainingProgress::getScenarioId)
                    .collect(Collectors.toSet());

            // 모든 시나리오가 완료되었는지 확인
            return completedScenarioIds.size() >= scenarios.size();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public int getTrainingAccuracy(Training training, Users user) {
        List<TrainingProgress> progress = trainingProgressRepository.findByUserAndTraining(user, training);
        if (progress.isEmpty()) {
            return 0;
        }

        return (int) progress.stream()
                .mapToInt(TrainingProgress::getScore)
                .average()
                .orElse(0);
    }

    public Map<String, Object> getProgressSummary(Users user) {
        List<Training> allTrainings = trainingRepository.findAll();
        List<TrainingProgress> userProgress = trainingProgressRepository.findByUser(user);

        int completedTrainings = 0;
        double totalAccuracy = 0.0;

        // 각 훈련별로 완료 여부와 정확도 계산
        for (Training training : allTrainings) {
            try {
                // trainingData에서 시나리오 목록 가져오기
                Map<String, Object> trainingData = new ObjectMapper().readValue(training.getTrainingData(), Map.class);
                List<Map<String, Object>> scenarios = (List<Map<String, Object>>) trainingData.get("scenarios");

                if (scenarios == null || scenarios.isEmpty()) {
                    continue;
                }

                // 이 훈련의 사용자 진행상황
                List<TrainingProgress> trainingProgress = userProgress.stream()
                        .filter(p -> p.getTraining().getId().equals(training.getId()))
                        .collect(Collectors.toList());

                // 완료한 시나리오 수 확인
                Set<String> completedScenarioIds = trainingProgress.stream()
                        .map(TrainingProgress::getScenarioId)
                        .collect(Collectors.toSet());

                // 모든 시나리오가 완료되었는지 확인
                if (completedScenarioIds.size() >= scenarios.size()) {
                    completedTrainings++;

                    // 이 훈련의 평균 점수 계산
                    double trainingAccuracy = trainingProgress.stream()
                            .mapToInt(TrainingProgress::getScore)
                            .average()
                            .orElse(0.0);

                    totalAccuracy += trainingAccuracy;
                }
            } catch (Exception e) {
                // JSON 파싱 오류 등 처리
                e.printStackTrace();
            }
        }

        // 전체 정확도 계산 (완료된 훈련이 있는 경우에만)
        int accuracy = completedTrainings > 0 ? (int) (totalAccuracy / completedTrainings) : 0;

        Map<String, Object> summary = new HashMap<>();
        summary.put("totalProgress", allTrainings.isEmpty() ? 0.0
                : Math.round((double) completedTrainings / allTrainings.size() * 10) / 10.0);
        summary.put("completedTraining", completedTrainings);
        summary.put("remainingTraining", allTrainings.size() - completedTrainings);
        summary.put("accuracy", accuracy);

        return summary;
    }
}
