package ru.labs.grading.controllers.student.services;

import io.grpc.ManagedChannel;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.labs.grading.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Service
public class TeacherServiceImpl implements TeacherService {

    private final ManagedChannel managedChannel;

    @Autowired
    public TeacherServiceImpl(ManagedChannel managedChannel) {
        this.managedChannel = managedChannel;
    }

    @Override
    public void sayBye() {
        ByeServiceGrpc.ByeServiceBlockingStub stub =
                ByeServiceGrpc.newBlockingStub(managedChannel);

        ByeServiceOuterClass.ByeRequest request = ByeServiceOuterClass.ByeRequest
                .newBuilder()
                .setByeName("Bye, Ivan")
                .build();

        ByeServiceOuterClass.ByeResponse response = stub.byeGreeting(request);
        System.out.println(response);
    }


    @Override
    public ByteArrayOutputStream getStudentFile(String taskId) {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final CountDownLatch finishLatch = new CountDownLatch(1);
        final AtomicBoolean completed = new AtomicBoolean(false);
        final FileServiceGrpc.FileServiceStub nonBlockingStub = FileServiceGrpc.newStub(managedChannel);
        StreamObserver<DataChunk> streamObserver = new StreamObserver<DataChunk>() {
            @Override
            public void onNext(DataChunk dataChunk) {
                try {
                    baos.write(dataChunk.getData().toByteArray());
                } catch (IOException e) {
                    log.error("error on write to byte array stream", e);
                    onError(e);
                }
            }
            @Override
            public void onError(Throwable t) {
                log.error("downloadFile() error", t);
                finishLatch.countDown();
            }
            @Override
            public void onCompleted() {
                log.info("downloadFile() has been completed!");
                completed.compareAndSet(false, true);
                finishLatch.countDown();
            }
        };

        try {
            DownloadFileRequest.Builder builder = DownloadFileRequest
                    .newBuilder()
                    .setFileName(taskId);
            nonBlockingStub.downloadFile(builder.build(), streamObserver);
            finishLatch.await(5, TimeUnit.MINUTES);
            if (!completed.get()) {
                throw new Exception("The downloadFile() method did not complete");
            }
        } catch (Exception e) {
            log.error("The downloadFile() method did not complete");
        }
        return baos;
    }
}
