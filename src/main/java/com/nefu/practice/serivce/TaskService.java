package com.nefu.practice.serivce;

import com.nefu.practice.entity.Task;
import com.nefu.practice.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class TaskService {
    @Autowired
    private TaskRepository taskRepository;

    public String createTask(String baseImageId, String targetImageId) {
        Task task = new Task();
        task.setId(UUID.randomUUID().toString());
        task.setBaseImageId(baseImageId);
        task.setTargetImageId(targetImageId);
        task.setStatus("waiting");
        task.setProgress(0);
        task.setCreateTime(LocalDateTime.now());

        taskRepository.save(task);
        return task.getId();
    }



    public void markRunning(String taskId) {
        Task task = get(taskId) ;
        task.setStatus("running");
        task.setProgress(0);
        taskRepository.save(task);
    }

    public void updateProgress(String taskId, int progress) {
        Task task = get(taskId);
        task.setProgress(progress);
        taskRepository.save(task);
    }

    public void markSuccess(String taskId, String resultPath) {
        Task task = get(taskId);
        task.setStatus("success");
        task.setProgress(100);
        task.setResultPath(resultPath);
        task.setFinishTime(LocalDateTime.now());
        taskRepository.save(task);
    }

    public void markError(String taskId) {
        Task task = get(taskId);
        task.setStatus("error");
        task.setFinishTime(LocalDateTime.now());
        taskRepository.save(task);
    }

    public Task get(String taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found: " + taskId));
    }


}
