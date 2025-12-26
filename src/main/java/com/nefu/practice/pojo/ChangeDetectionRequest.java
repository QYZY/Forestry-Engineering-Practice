package com.nefu.practice.pojo;

import lombok.Data;

@Data
public class ChangeDetectionRequest {
    private String baseImageId;
    private String targetImageId;
}
