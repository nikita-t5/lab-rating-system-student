package ru.labs.grading.controllers.student.services;

import com.google.protobuf.ByteString;
import io.grpc.ManagedChannel;
import io.grpc.stub.StreamObserver;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.labs.grading.*;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.Objects;

@Slf4j
@Service
public class StudentServiceImpl implements StudentService {

    private final ManagedChannel managedChannel;

    @Autowired
    public StudentServiceImpl(ManagedChannel managedChannel) {
        this.managedChannel = managedChannel;
    }

    @Override
    public void sayHello() {
        //создали канал передачи и приема данными
//        ManagedChannel channel = ManagedChannelBuilder.forTarget("localhost:8090")
//                .usePlaintext()
//                .build();

        //создание объекта stab - на нем делаем удаленные запросы
        //stub сделает вызов метода по сети и вернет ответ
        GreetingServiceGrpc.GreetingServiceBlockingStub stub =
                GreetingServiceGrpc.newBlockingStub(managedChannel);
        //создаем объект реквеста
        GreetingServiceOuterClass.HelloRequest request = GreetingServiceOuterClass.HelloRequest
                .newBuilder()
                .setName("Ivan")
                .build();
        //удаленный вызов процедуры
        GreetingServiceOuterClass.HelloResponse response = stub.greeting(request);

        System.out.println(response);
//        channel.shutdownNow();
    }


    @SneakyThrows
    @Override
    public String postFile(MultipartFile file, String developerFullName) {
        FileUploadServiceGrpc.FileUploadServiceStub stub = FileUploadServiceGrpc.newStub(managedChannel);
        StreamObserver<FileUploadRequest> streamObserver = stub.uploadFile(new FileUploadObserver());

        //разделить название файла для выделения его типа
        final String[] fileNameAndType = Objects.requireNonNull(file.getOriginalFilename()).split("\\."); // Разделения строки str с помощью метода split()

        // build metadata
        FileUploadRequest metadata = FileUploadRequest.newBuilder()
                .setMetadata(MetaData.newBuilder()
                        .setName(fileNameAndType[0])
                        .setType(fileNameAndType[1])
                        .setDeveloperFullName(developerFullName)
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
        return null;
    }

    private static class FileUploadObserver implements StreamObserver<FileUploadResponse> {
        String taskId;
        @Override
        public void onNext(FileUploadResponse fileUploadResponse) {
            log.info("File upload status :: {}", fileUploadResponse.getStatus());
            taskId = fileUploadResponse.getName();
        }

        @Override
        public void onError(Throwable throwable) {
            log.error("onError");
        }

        @Override
        public void onCompleted() {
            log.info("File with taskID :: {} on Completed", taskId);
        }
    }
}
