package ru.labs.grading.controllers.student.services;

import com.google.protobuf.Empty;
import io.grpc.ManagedChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.labs.grading.MinRatingServiceGrpc;
import ru.labs.grading.MinRatingServiceOuterClass;
import ru.labs.grading.TaskServiceGrpc;
import ru.labs.grading.TaskServiceOuterClass;
import ru.labs.grading.controllers.student.dto.LoadedTaskDTO;

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
    public List<LoadedTaskDTO> getAllTask() {
        List<LoadedTaskDTO> allTask = new ArrayList<>();
        TaskServiceGrpc.TaskServiceBlockingStub stub =
                TaskServiceGrpc.newBlockingStub(managedChannel);
        TaskServiceOuterClass.TaskListResponse response =
                stub.getAllTask(Empty.newBuilder().build());
        List<TaskServiceOuterClass.Task> taskListResponse = response.getTaskList();
        for (TaskServiceOuterClass.Task task: taskListResponse){
            allTask.add(new LoadedTaskDTO(task.getTaskId(), task.getDeveloperFullName()));
        }
        return allTask;
    }
}
