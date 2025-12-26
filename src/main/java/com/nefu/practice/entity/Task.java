package com.nefu.practice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "task")
@Data
public class Task {
    @Id
    private String id;

    private String baseImageId;
    private String targetImageId;

    private String status;     // waiting / running / success / error
    private Integer progress;

    private String resultPath;

    private LocalDateTime createTime;
    private LocalDateTime finishTime;
}
