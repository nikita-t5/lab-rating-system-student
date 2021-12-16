package ru.labs.grading.controllers.student.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.labs.grading.controllers.student.services.StudentService;

@Slf4j
@RestController
@RequestMapping(value = "/student/personal", consumes = {MediaType.ALL_VALUE},
        produces = MediaType.ALL_VALUE)
public class StudentPersonalController {


    private final StudentService studentService;

    @Autowired
    public StudentPersonalController(StudentService studentService) {
        this.studentService = studentService;
    }

    //    private final TeacherService teacherService;

//    @Autowired
//    public StudentPersonalController(StudentService studentService, TeacherService teacherService) {
//        this.studentService = studentService;
//        this.teacherService = teacherService;
//    }

    //Получить средню оценку своей работы
    @GetMapping
    public String getAverageRating(@RequestParam String taskId) {
        studentService.sayHello();
        return "123" + taskId;
    }


    //потом удали это!
//    @PostMapping("personal")
//    public String postFileeeeeeeeeeee(@RequestParam("file") MultipartFile file) {
//        teacherService.sayBye();
//        return String.valueOf(UUID.randomUUID());
//    }


    //загр файл и ФИО на сервер и получить taskID
    @PostMapping
    public String postFile(@RequestParam("file") MultipartFile file, String developerFullName) {
        return studentService.postFile(file, developerFullName);
    }


///для теста
//    @SneakyThrows
//    @PostMapping("personal/load")
//    public ResponseEntity<byte[]> postFile(@RequestParam("file") MultipartFile file, String fullName) {
//        HttpHeaders httpHeaders = new HttpHeaders();
//        httpHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE); // (3) Content-Type: application/octet-stream
//        httpHeaders.set(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename("demo-file.txt").build().toString()); // (4) Content-Disposition: attachment; filename="demo-file.txt"
//
////        studentService.postFile(file,fullName);
//        return ResponseEntity.ok().headers(httpHeaders).body(file.getBytes()); // (5) Return Response
//    }


}
