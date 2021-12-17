package ru.labs.grading.services;

import com.google.protobuf.Empty;
import io.grpc.ManagedChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.labs.grading.EvaluationTaskServiceGrpc;
import ru.labs.grading.EvaluationTaskServiceOuterClass;
import ru.labs.grading.TaskServiceGrpc;
import ru.labs.grading.TaskServiceOuterClass;
import ru.labs.grading.dto.EvaluationDTO;
import ru.labs.grading.dto.LoadedTaskDTO;

import java.util.ArrayList;
import java.util.List;

@Service
public class TeacherServiceImpl implements TeacherService {

    private final EvaluationTaskServiceGrpc.EvaluationTaskServiceBlockingStub evaluationTaskServiceBlockingStub;

    private final TaskServiceGrpc.TaskServiceBlockingStub taskServiceBlockingStub;

    @Autowired
    public TeacherServiceImpl(ManagedChannel managedChannel) {
        this.evaluationTaskServiceBlockingStub = EvaluationTaskServiceGrpc.newBlockingStub(managedChannel);
        this.taskServiceBlockingStub = TaskServiceGrpc.newBlockingStub(managedChannel);
    }

    @Override
    public List<EvaluationDTO> getAllEvaluation(String taskId) {
        List<EvaluationDTO> evaluationDTOList = new ArrayList<>();
        EvaluationTaskServiceOuterClass.EvaluationRequest request = EvaluationTaskServiceOuterClass.EvaluationRequest
                .newBuilder()
                .setTaskId(taskId)
                .build();
        EvaluationTaskServiceOuterClass.EvaluationResponse response = evaluationTaskServiceBlockingStub.getAllEvaluationTask(request);
        List<EvaluationTaskServiceOuterClass.Evaluation> evaluationList = response.getEvaluationList();
        for (EvaluationTaskServiceOuterClass.Evaluation evaluation : evaluationList) {
            evaluationDTOList.add(new EvaluationDTO(evaluation.getTaskId(), evaluation.getAppraiserFullName(), evaluation.getRating()));
        }
        return evaluationDTOList;
    }

    @Override
    public List<LoadedTaskDTO> getAllTask() {
        List<LoadedTaskDTO> allTask = new ArrayList<>();
        TaskServiceOuterClass.TaskListResponse response = taskServiceBlockingStub.getAllTask(Empty.newBuilder().build());
        List<TaskServiceOuterClass.Task> taskListResponse = response.getTaskList();
        for (TaskServiceOuterClass.Task task : taskListResponse) {
            allTask.add(new LoadedTaskDTO(task.getTaskId(), task.getDeveloperFullName()));
        }
        return allTask;
    }
}
