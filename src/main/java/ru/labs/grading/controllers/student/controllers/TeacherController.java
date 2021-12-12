package ru.labs.grading.controllers.student.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.labs.grading.controllers.student.dto.Evaluation;
import ru.labs.grading.controllers.student.dto.LoadedWork;
import ru.labs.grading.controllers.student.services.TeacherService;

import java.io.ByteArrayOutputStream;
import java.util.List;

@RestController
@RequestMapping(value = "/teacher", consumes = {MediaType.ALL_VALUE},
        produces = MediaType.ALL_VALUE)
public class TeacherController {

    private final TeacherService teacherService;

    @Autowired
    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }


    //получить спиок всех загруженных работ
    @GetMapping
    public List<LoadedWork> getListLoadedWork() {
        return null;
    }

    //выгрузить работу студента
    @GetMapping
    public ResponseEntity<byte[]> getStudentFile(@RequestParam String taskId) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE); // (3) Content-Type: application/octet-stream
        httpHeaders.set(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename("demo-file.txt").build().toString());
        ByteArrayOutputStream baos = teacherService.getStudentFile(taskId);
        return ResponseEntity.ok().headers(httpHeaders).body(baos.toByteArray()); // (5) Return Response
//        return "123" + taskId;
    }

    //получить все полученые оценки по конкретной работе
    @GetMapping("appraisers")
    public List<Evaluation> getListEvaluationTask(@RequestParam String taskId) {
        return null;
    }

    @GetMapping("rating")
    public Double getAverageRating(@RequestParam String taskId) {
        return null;
    }


}