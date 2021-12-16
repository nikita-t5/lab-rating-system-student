package ru.labs.grading.controllers.student.services;

import java.io.ByteArrayOutputStream;

public interface CommonService {
//    void sayBye();
    ByteArrayOutputStream getStudentFile(String taskId);

    Double getAverageRating(String taskId);

}
