package ru.labs.grading.services;

import io.grpc.ManagedChannel;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.labs.grading.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Service
public class CommonServiceImpl implements CommonService {

    private final ManagedChannel managedChannel;

    @Autowired
    public CommonServiceImpl(ManagedChannel managedChannel) {
        this.managedChannel = managedChannel;
    }

    @Override
    public Double getAverageRating(String taskId) {
        AverageRatingServiceGrpc.AverageRatingServiceBlockingStub stub =
                AverageRatingServiceGrpc.newBlockingStub(managedChannel);

        AverageRatingServiceOuterClass.AverageRatingRequest request = AverageRatingServiceOuterClass.AverageRatingRequest
                .newBuilder()
                .setTaskId(taskId)
                .build();
        AverageRatingServiceOuterClass.AverageRatingResponse response = stub.getAverageRatingByTaskId(request);
        return response.getAverageRating();
    }

    @Override
    public ByteArrayOutputStream getStudentFile(String taskId) {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final CountDownLatch finishLatch = new CountDownLatch(1);
        final AtomicBoolean completed = new AtomicBoolean(false);
        final FileServiceGrpc.FileServiceStub nonBlockingStub = FileServiceGrpc.newStub(managedChannel);
        log.info("Start download file with taskId :: {}", taskId);
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
            public void onError(Throwable throwable) {
                log.error("download File error", throwable);
                finishLatch.countDown();
            }

            @Override
            public void onCompleted() {
                log.info("download File has been completed!");
                completed.compareAndSet(false, true);
                finishLatch.countDown();
            }
        };

        try {
            DownloadFileRequest.Builder builder = DownloadFileRequest
                    .newBuilder()
                    .setTaskId(taskId);
            nonBlockingStub.downloadFile(builder.build(), streamObserver);
            finishLatch.await(5, TimeUnit.MINUTES);
            if (!completed.get()) {
                throw new Exception("The downloadFile() method did not complete");
            }
        } catch (Exception e) {
            log.error("The downloadFile() method did not complete");
        }
        log.info("Finish download file with taskId :: {}", taskId);
        return baos;
    }
}