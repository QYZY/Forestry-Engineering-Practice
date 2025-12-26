package com.nefu.practice.controller;

import com.nefu.practice.common.Result;
import com.nefu.practice.pojo.ChangeDetectionRequest;
import com.nefu.practice.pojo.ChangeDetectionResponse;
import com.nefu.practice.serivce.ChangeDetectionService;
import com.nefu.practice.serivce.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/analysis")
public class AnalysisController {

    @Autowired
    private ChangeDetectionService changeDetectionService;

    @PostMapping("/change-detection")
    public Result<ChangeDetectionResponse> startChangeDetection(@RequestBody ChangeDetectionRequest req) {



        String taskId = changeDetectionService.startDetection(req.getBaseImageId(),  req.getTargetImageId());

        ChangeDetectionResponse response = new ChangeDetectionResponse();
        response.setTaskId(taskId);

        return Result.success(response);
    }
}
