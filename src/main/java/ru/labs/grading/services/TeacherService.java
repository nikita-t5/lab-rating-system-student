package ru.labs.grading.services;

import ru.labs.grading.dto.EvaluationDTO;
import ru.labs.grading.dto.LoadedTaskDTO;

import java.util.List;

public interface TeacherService {
    List<LoadedTaskDTO> getAllTask();

    List<EvaluationDTO> getAllEvaluation(String taskId);
}
