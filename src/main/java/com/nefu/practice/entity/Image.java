package com.nefu.practice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "image")
@Data
public class Image {
    @Id
    private String id;

    private String name;

    @Column(name = "file_path")
    private String filePath;


    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "upload_time")
    private LocalDateTime uploadTime;

    private String remark;

}
