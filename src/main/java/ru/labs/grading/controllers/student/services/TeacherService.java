package ru.labs.grading.controllers.student.services;

import ru.labs.grading.controllers.student.dto.EvaluationDTO;
import ru.labs.grading.controllers.student.dto.LoadedTaskDTO;

import java.util.List;

public interface TeacherService {
    List<LoadedTaskDTO> getAllTask();

    List<EvaluationDTO> getAllEvaluation(String taskId);
}
