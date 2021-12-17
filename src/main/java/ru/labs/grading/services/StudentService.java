package ru.labs.grading.services;

import org.springframework.web.multipart.MultipartFile;
import ru.labs.grading.dto.EvaluationDTO;

import java.util.List;

public interface StudentService {
    String postFile(MultipartFile file, String developerFullName);

    String postRatingByEvaluationDTO(EvaluationDTO evaluationDTO);

    List<String> getMinRatingTask();
}
