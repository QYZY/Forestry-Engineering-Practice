package com.nefu.practice.pojo;

import lombok.Data;

@Data
public class TaskInfo {

    private String taskId;
    private String status;   // running | success | error
    private int progress;    // 0 ~ 100
    private String resultUrl;

    public TaskInfo(String taskId) {
        this.taskId = taskId;
        this.status = "running";
        this.progress = 0;
        this.resultUrl = "";
    }
}

