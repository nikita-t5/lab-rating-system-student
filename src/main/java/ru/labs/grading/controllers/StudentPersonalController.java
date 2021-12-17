package ru.labs.grading.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.labs.grading.services.CommonService;
import ru.labs.grading.services.StudentService;

@Slf4j
@RestController
@RequestMapping(value = "/student/personal", consumes = {MediaType.ALL_VALUE},
        produces = MediaType.ALL_VALUE)
public class StudentPersonalController {

    private final StudentService studentService;

    private final CommonService commonService;

    @Autowired
    public StudentPersonalController(StudentService studentService, CommonService commonService) {
        this.studentService = studentService;
        this.commonService = commonService;
    }

    //Получить средню оценку своей работы
    @GetMapping
    public String getAverageRating(@RequestParam String taskId) {
        Double averageRating = commonService.getAverageRating(taskId);
        return "For taskId " + taskId + " average rating = " + averageRating.toString();
    }

    //загр файл и ФИО на сервер и получить taskID
    @PostMapping
    public String postFile(@RequestParam("file") MultipartFile file, String developerFullName) {
        return studentService.postFile(file, developerFullName);
    }
}
