package ru.labs.grading.services;

import com.google.protobuf.Empty;
import io.grpc.ManagedChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.labs.grading.*;
import ru.labs.grading.dto.EvaluationDTO;
import ru.labs.grading.dto.LoadedTaskDTO;

import java.util.ArrayList;
import java.util.List;

@Service
public class TeacherServiceImpl implements TeacherService {

    private final ManagedChannel managedChannel;

    @Autowired
    public TeacherServiceImpl(ManagedChannel managedChannel) {
        this.managedChannel = managedChannel;
    }

    @Override
    public List<EvaluationDTO> getAllEvaluation(String taskId) {
        List<EvaluationDTO> evaluationDTOList = new ArrayList<>();
        EvaluationTaskServiceGrpc.EvaluationTaskServiceBlockingStub stub =
                EvaluationTaskServiceGrpc.newBlockingStub(managedChannel);
        EvaluationTaskServiceOuterClass.EvaluationRequest request = EvaluationTaskServiceOuterClass.EvaluationRequest
                .newBuilder()
                .setTaskId(taskId)
                .build();
        EvaluationTaskServiceOuterClass.EvaluationResponse response = stub.getAllEvaluationTask(request);
        List<EvaluationTaskServiceOuterClass.Evaluation> evaluationList = response.getEvaluationList();
        for (EvaluationTaskServiceOuterClass.Evaluation evaluation : evaluationList) {
            evaluationDTOList.add(new EvaluationDTO(evaluation.getTaskId(), evaluation.getAppraiserFullName(), evaluation.getRating()));
        }
        return evaluationDTOList;
    }

    @Override
    public List<LoadedTaskDTO> getAllTask() {
        List<LoadedTaskDTO> allTask = new ArrayList<>();
        TaskServiceGrpc.TaskServiceBlockingStub stub =
                TaskServiceGrpc.newBlockingStub(managedChannel);
        TaskServiceOuterClass.TaskListResponse response = stub.getAllTask(Empty.newBuilder().build());
        List<TaskServiceOuterClass.Task> taskListResponse = response.getTaskList();
        for (TaskServiceOuterClass.Task task : taskListResponse) {
            allTask.add(new LoadedTaskDTO(task.getTaskId(), task.getDeveloperFullName()));
        }
        return allTask;
    }
}
