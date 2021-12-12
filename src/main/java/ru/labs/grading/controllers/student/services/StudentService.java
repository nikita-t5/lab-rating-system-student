package ru.labs.grading.controllers.student.services;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public interface StudentService {
    void sayHello();
     String postFile(MultipartFile file, String developerFullName);
}
