package com.phishme.backend.controller;

import com.phishme.backend.entities.Training;
import com.phishme.backend.entities.TrainingProgress;
import com.phishme.backend.entities.Users;
import com.phishme.backend.enums.ErrorCode;
import com.phishme.backend.exceptions.BusinessException;
import com.phishme.backend.security.jwt.UserPrincipal;
import com.phishme.backend.service.TrainingService;
import com.phishme.backend.dto.SaveProgressRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/trainings")
@RequiredArgsConstructor
public class TrainingController {
    private final TrainingService trainingService;

    @GetMapping
    public ResponseEntity<List<Training>> getAllTrainings() {
        return ResponseEntity.ok(trainingService.getAllTrainings());
    }

    @PostMapping("/{trainingId}/progress")
    public ResponseEntity<Void> saveProgress(
            @PathVariable("trainingId") Long trainingId,
            @RequestBody SaveProgressRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        Users user = userPrincipal.getUser();
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_DOESNT_EXIST);
        }

        trainingService.saveProgress(trainingId, request.getScenarioId(), user, request.getScore());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{trainingId}/progress")
    public ResponseEntity<List<TrainingProgress>> getProgress(
            @PathVariable("trainingId") Long trainingId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        Users user = userPrincipal.getUser();
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_DOESNT_EXIST);
        }

        Training training = trainingService.getTrainingById(trainingId);
        return ResponseEntity.ok(trainingService.getUserProgress(user, training));
    }

    @GetMapping("/{trainingId}/completion")
    public ResponseEntity<Map<String, Object>> checkTrainingCompletion(
            @PathVariable("trainingId") Long trainingId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        Users user = userPrincipal.getUser();
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_DOESNT_EXIST);
        }

        Training training = trainingService.getTrainingById(trainingId);
        boolean isCompleted = trainingService.isTrainingCompleted(training, user);
        double accuracy = trainingService.getTrainingAccuracy(training, user);

        Map<String, Object> response = new HashMap<>();
        response.put("completed", isCompleted);
        response.put("accuracy", accuracy);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/progress/summary")
    public ResponseEntity<Map<String, Object>> getProgressSummary(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        Users user = userPrincipal.getUser();
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_DOESNT_EXIST);
        }

        return ResponseEntity.ok(trainingService.getProgressSummary(user));
    }
}
