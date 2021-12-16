package ru.labs.grading.controllers.student.services;

import org.springframework.web.multipart.MultipartFile;
import ru.labs.grading.controllers.student.dto.EvaluationDTO;

import java.util.List;

public interface StudentService {
    void sayHello();

    String postFile(MultipartFile file, String developerFullName);

    String postRatingByEvaluationDTO(EvaluationDTO evaluationDTO);

    List<String> getMinRatingTask();
}
