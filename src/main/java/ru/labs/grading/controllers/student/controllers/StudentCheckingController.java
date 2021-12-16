package ru.labs.grading.controllers.student.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.labs.grading.controllers.student.dto.EvaluationDTO;
import ru.labs.grading.controllers.student.services.CommonService;
import ru.labs.grading.controllers.student.services.StudentService;

import java.io.ByteArrayOutputStream;
import java.util.List;

@RestController
@RequestMapping(value = "/student/checking", consumes = {MediaType.ALL_VALUE},
        produces = MediaType.ALL_VALUE)
public class StudentCheckingController {

    private final CommonService commonService;

    private final StudentService studentService;

    @Autowired
    public StudentCheckingController(CommonService commonService, StudentService studentService) {
        this.commonService = commonService;
        this.studentService = studentService;
    }
    //получить 3 шт taskId для оценки работы других студентов (не свою)
    @GetMapping("/task")
    public List<String> getListTaskId() {
        return null;
    }

    //выгрузить работу др студента(не свою)
    @GetMapping
    public ResponseEntity<byte[]> getStudentFileForCheck(@RequestParam String taskId) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
        httpHeaders.set(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename("demo-file.txt").build().toString());
        ByteArrayOutputStream baos = commonService.getStudentFile(taskId);
        return ResponseEntity.ok().headers(httpHeaders).body(baos.toByteArray());
    }

    //оценить работу др студента
    @PostMapping
    public String evaluateAnotherStudentWork(@RequestBody EvaluationDTO evaluationDTO){
        if(evaluationDTO.getRating() < 2 || evaluationDTO.getRating() > 5){
            return "Incorrect rating. Set rating from 2 to 5";
        }
        String responseFromServer = studentService.postRatingByEvaluationDTO(evaluationDTO);
        return "you have rated the task" + responseFromServer;
    }
}
