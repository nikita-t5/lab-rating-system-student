package ru.labs.grading.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.labs.grading.dto.EvaluationDTO;
import ru.labs.grading.dto.LoadedTaskDTO;
import ru.labs.grading.services.CommonService;
import ru.labs.grading.services.TeacherService;

import java.io.ByteArrayOutputStream;
import java.util.List;

@RestController
@RequestMapping(value = "/teacher", consumes = {MediaType.ALL_VALUE},
        produces = MediaType.ALL_VALUE)
public class TeacherController {

    private final CommonService commonService;

    private final TeacherService teacherService;

    @Autowired
    public TeacherController(CommonService commonService, TeacherService teacherService) {
        this.commonService = commonService;
        this.teacherService = teacherService;
    }


    //получить спиок всех загруженных работ
    @GetMapping("works")
    public ResponseEntity<List<LoadedTaskDTO>> getListLoadedWork() {
        List<LoadedTaskDTO> allLoadedTaskList = teacherService.getAllTask();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        return ResponseEntity.ok().headers(httpHeaders).body(allLoadedTaskList);
    }

    //выгрузить работу студента
    @GetMapping
    public ResponseEntity<byte[]> getStudentFile(@RequestParam String taskId) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
        httpHeaders.set(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename("demo-file.txt").build().toString());
        ByteArrayOutputStream baos = commonService.getStudentFile(taskId);
        return ResponseEntity.ok().headers(httpHeaders).body(baos.toByteArray());
    }

    //получить все полученые оценки по конкретной работе
    @GetMapping("appraisers")
    public ResponseEntity<List<EvaluationDTO>> getListEvaluationTask(@RequestParam String taskId) {
        List<EvaluationDTO> allEvaluationList = teacherService.getAllEvaluation(taskId);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        return ResponseEntity.ok().headers(httpHeaders).body(allEvaluationList);
    }

    @GetMapping("rating")
    public String getAverageRating(@RequestParam String taskId) {
        Double averageRating = commonService.getAverageRating(taskId);
        return "For taskId " + taskId + " average rating = " + averageRating.toString();
    }
}
