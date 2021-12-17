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

    private final MinRatingServiceGrpc.MinRatingServiceBlockingStub minRatingServiceBlockingStub;

    private final PostRatingServiceGrpc.PostRatingServiceBlockingStub postRatingServiceBlockingStub;

    private final FileUploadServiceGrpc.FileUploadServiceStub fileUploadServiceStub;


    @Autowired
    public StudentServiceImpl(ManagedChannel managedChannel) {
        this.minRatingServiceBlockingStub = MinRatingServiceGrpc.newBlockingStub(managedChannel);
        this.postRatingServiceBlockingStub = PostRatingServiceGrpc.newBlockingStub(managedChannel);
        this.fileUploadServiceStub = FileUploadServiceGrpc.newStub(managedChannel);
    }

    @Override
    public List<String> getMinRatingTask() {
        MinRatingServiceOuterClass.MinRatingResponse response =
                minRatingServiceBlockingStub.getMinRatingList(Empty.newBuilder().build());
        return response.getTaskIdList();
    }

    @Override
    public String postRatingByEvaluationDTO(EvaluationDTO evaluationDTO) {
        PostRatingServiceOuterClass.PostRatingRequest request = PostRatingServiceOuterClass.PostRatingRequest
                .newBuilder()
                .setTaskId(evaluationDTO.getTaskId())
                .setAppraiserFullName(evaluationDTO.getAppraiserFullName())
                .setRating(evaluationDTO.getRating())
                .build();
        PostRatingServiceOuterClass.PostRatingResponse response = postRatingServiceBlockingStub.postRatingByEvaluationDto(request);
        return response.getTaskIdResponse();
    }

    @SneakyThrows
    @Override
    public String postFile(MultipartFile file, String developerFullName) {
        StreamObserver<FileUploadRequest> streamObserver = fileUploadServiceStub.uploadFile(new FileUploadObserver());

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
