package ru.labs.client.controllers.student;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping(value = "/student", consumes = {MediaType.ALL_VALUE},
        produces = MediaType.ALL_VALUE)
public class PersonalController {

    @GetMapping("personal")
    public String getAverageRating() {
        return "123";
    }


    @PostMapping("personal")
    public String postFile(@RequestParam("file") MultipartFile file) {
        return String.valueOf(UUID.randomUUID());
    }

}
