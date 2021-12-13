package ru.labs.grading.controllers.student.services;

import com.google.protobuf.ByteString;
import io.grpc.ManagedChannel;
import io.grpc.stub.StreamObserver;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.labs.grading.*;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.Objects;

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
////        old
////        FileOwnServiceGrpc.FileOwnServiceBlockingStub stub =
////                FileOwnServiceGrpc.newBlockingStub(managedChannel);
////        FileOwnServiceOuterClass.FileOwnRequest request = FileOwnServiceOuterClass.FileOwnRequest
////                .newBuilder()
////                .setFullName("lalala")
////                .setData(ByteString.copyFrom(file.getBytes()))
////                .build();
////        FileOwnServiceOuterClass.FileOwnResponse response =
////                stub.loadOwnFile(request);
////        return null;
//
//        //new
//        FileServiceGrpc.FileServiceStub fileServiceStub = FileServiceGrpc.newStub(managedChannel);
//
//        StreamObserver<FileUploadRequest> streamObserver = fileServiceStub.upload(new FileUploadObserver());
//
//        FileUploadRequest metadata = FileUploadRequest.newBuilder()
//                .setMetadata(MetaData.newBuilder()
//                        .setName("My student file")
//                        .setType("txt").build())
//                .build();
//        streamObserver.onNext(metadata);
//
//        InputStream inputStream =  new BufferedInputStream(file.getInputStream());
//        byte[] bytes = new byte[4096];
//
//        int size;
//        while ((size = inputStream.read(bytes)) > 0){
//            FileUploadRequest uploadRequest = FileUploadRequest.newBuilder()
//                    .setFile(File.newBuilder().setContent(ByteString.copyFrom(bytes, 0 , size)).build())
//                    .build();
//            streamObserver.onNext(uploadRequest);
//        }
//        inputStream.close();
//        streamObserver.onCompleted();
//
//
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

//        InputStream inputStream = Files.newInputStream(path);
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
        @Override
        public void onNext(FileUploadResponse fileUploadResponse) {
            System.out.println(
                    "File upload status :: " + fileUploadResponse.getStatus()
            );
        }

        @Override
        public void onError(Throwable throwable) {
            System.out.println("onError");
        }

        @Override
        public void onCompleted() {
            System.out.println("onCompleted");
        }
    }


//    private static class FileUploadObserver implements StreamObserver<FileUploadResponse> {
//        @Override
//        public void onNext(FileUploadResponse fileUploadResponse) {
//            System.out.println(
//                    "File upload status :: " + fileUploadResponse.getStatus()
//            );
//        }
//
//        @Override
//        public void onError(Throwable throwable) {
//
//        }
//
//        @Override
//        public void onCompleted() {
//
//        }
//    }


}
