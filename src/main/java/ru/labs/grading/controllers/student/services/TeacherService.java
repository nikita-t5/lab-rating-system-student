package ru.labs.grading.controllers.student.services;

import java.io.ByteArrayOutputStream;

public interface TeacherService {
    void sayBye();
    ByteArrayOutputStream getStudentFile(String taskId);
}
