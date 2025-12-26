package com.nefu.practice.serivce;

import com.nefu.practice.constant.TaskStatus;
import com.nefu.practice.pojo.TaskInfo;
import com.nefu.practice.repository.ImageRepository;
import com.nefu.practice.repository.TaskRepository;
import com.nefu.practice.serivce.worker.ChangeDetectionWorker;
import com.nefu.practice.util.ImageDiffUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class ChangeDetectionService {


    @Autowired
    private TaskService taskService;

    @Autowired
    private ChangeDetectionWorker worker;

    @Autowired
    @Qualifier("changeDetectionExecutor")
    private ExecutorService executor;

    public String startDetection(String baseImageId, String targetImageId) {
        String taskId = taskService.createTask(baseImageId, targetImageId);

        // ⭐ 关键：异步执行
        executor.submit(() -> worker.process(taskId));

        return taskId;
    }


}