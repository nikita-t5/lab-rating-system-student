package ru.labs.grading.controllers.student.services;

import java.io.ByteArrayOutputStream;

public interface CommonService {
    ByteArrayOutputStream getStudentFile(String taskId);

    Double getAverageRating(String taskId);
}
