package com.nefu.practice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nefu.practice.serivce.ChangeDetectionService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AnalysisController.class)
class AnalysisControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ChangeDetectionService changeDetectionService;

    @Test
    void startChangeDetection_shouldReturnTaskId() throws Exception {
        Mockito.when(changeDetectionService.startDetection(anyString(), anyString()))
                .thenReturn("task-123");

        String body = objectMapper.writeValueAsString(new java.util.HashMap<String, Object>() {{
            put("baseImageId", "imgA");
            put("targetImageId", "imgB");
        }});

        mockMvc.perform(post("/api/analysis/change-detection")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.taskId").value("task-123"));
    }
}

