package com.nefu.practice.controller;

import com.nefu.practice.common.Result;
import com.nefu.practice.entity.Task;
import com.nefu.practice.pojo.TaskInfo;
import com.nefu.practice.repository.TaskRepository;
import com.nefu.practice.serivce.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/task")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @GetMapping("/{taskId}/status")
    public Result<Task> getStatus(@PathVariable String taskId) {

        Task task = taskService.get(taskId);

        return Result.success(task);
    }
}
