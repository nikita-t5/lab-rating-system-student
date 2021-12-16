package ru.labs.grading.controllers.student.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationDTO {
    private String taskId;
    private String appraiserFullName;
    private Integer rating;
}
