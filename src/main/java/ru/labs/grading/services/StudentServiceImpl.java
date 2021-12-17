package ru.labs.grading.services;

import com.google.protobuf.ByteString;
import com.google.protobuf.Empty;
import io.grpc.ManagedChannel;
import io.grpc.stub.StreamObserver;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.labs.grading.*;
import ru.labs.grading.dto.EvaluationDTO;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
public class StudentServiceImpl implements StudentService {

    private final ManagedChannel managedChannel;

    @Autowired
    public StudentServiceImpl(ManagedChannel managedChannel) {
        this.managedChannel = managedChannel;
    }

//    @Override
//    public void sayHello() {
    //создали канал передачи и приема данными
//        ManagedChannel channel = ManagedChannelBuilder.forTarget("localhost:8090")
//                .usePlaintext()
//                .build();

    //создание объекта stab - на нем делаем удаленные запросы
    //stub сделает вызов метода по сети и вернет ответ
//        GreetingServiceGrpc.GreetingServiceBlockingStub stub =
//                GreetingServiceGrpc.newBlockingStub(managedChannel);
//        //создаем объект реквеста
//        GreetingServiceOuterClass.HelloRequest request = GreetingServiceOuterClass.HelloRequest
//                .newBuilder()
//                .setName("Ivan")
//                .build();
//        //удаленный вызов процедуры
//        GreetingServiceOuterClass.HelloResponse response = stub.greeting(request);
//
//        System.out.println(response);
////        channel.shutdownNow();
//    }


    @Override
    public List<String> getMinRatingTask() {
        MinRatingServiceGrpc.MinRatingServiceBlockingStub stub =
                MinRatingServiceGrpc.newBlockingStub(managedChannel);
        MinRatingServiceOuterClass.MinRatingResponse response =
                stub.getMinRatingList(Empty.newBuilder().build());
        return response.getTaskIdList();
    }

    @Override
    public String postRatingByEvaluationDTO(EvaluationDTO evaluationDTO) {
        PostRatingServiceGrpc.PostRatingServiceBlockingStub stub =
                PostRatingServiceGrpc.newBlockingStub(managedChannel);
        PostRatingServiceOuterClass.PostRatingRequest request = PostRatingServiceOuterClass.PostRatingRequest
                .newBuilder()
                .setTaskId(evaluationDTO.getTaskId())
                .setAppraiserFullName(evaluationDTO.getAppraiserFullName())
                .setRating(evaluationDTO.getRating())
                .build();
        PostRatingServiceOuterClass.PostRatingResponse response = stub.postRatingByEvaluationDto(request);
        return response.getTaskIdResponse();
    }


    @SneakyThrows
    @Override
    public String postFile(MultipartFile file, String developerFullName) {
        FileUploadServiceGrpc.FileUploadServiceStub stub = FileUploadServiceGrpc.newStub(managedChannel);
        StreamObserver<FileUploadRequest> streamObserver = stub.uploadFile(new FileUploadObserver());

        //разделить название файла для выделения его типа
        final String[] fileNameAndType = Objects.requireNonNull(file.getOriginalFilename()).split("\\."); // Разделения строки str с помощью метода split()

        final String taskId = UUID.randomUUID().toString();
        log.info("Start file upload with taskId :: {}", taskId);


        // build metadata
        FileUploadRequest metadata = FileUploadRequest.newBuilder()
                .setMetadata(MetaData.newBuilder()
                        .setName(fileNameAndType[0])
                        .setType(fileNameAndType[1])
                        .setDeveloperFullName(developerFullName)
                        .setTaskId(taskId)
                        .build())
                .build();
        streamObserver.onNext(metadata);

        // upload bytes
        InputStream inputStream = new BufferedInputStream(file.getInputStream());

        byte[] bytes = new byte[4096];
        int size;
        while ((size = inputStream.read(bytes)) > 0) {
            FileUploadRequest uploadRequest = FileUploadRequest.newBuilder()
                    .setFile(File.newBuilder().setContent(ByteString.copyFrom(bytes, 0, size)).build())
                    .build();
            streamObserver.onNext(uploadRequest);
        }
        // close the stream
        inputStream.close();
        streamObserver.onCompleted();
        log.info("Finish file upload with taskId :: {}", taskId);
        return taskId;
    }

    private static class FileUploadObserver implements StreamObserver<FileUploadResponse> {
        @Override
        public void onNext(FileUploadResponse fileUploadResponse) {
            log.info("File upload status :: {}", fileUploadResponse.getStatus());
        }

        @Override
        public void onError(Throwable throwable) {
            log.error("upload File error", throwable);
        }

        @Override
        public void onCompleted() {
            log.info("upload File has been completed!");
        }
    }
}
