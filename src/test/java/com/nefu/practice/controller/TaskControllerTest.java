package com.nefu.practice.controller;

import com.nefu.practice.entity.Task;
import com.nefu.practice.serivce.TaskService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @Test
    void getStatus_shouldReturnTask() throws Exception {
        Task task = new Task();
        task.setId("task-1");
        task.setStatus("success");
        task.setProgress(100);
        task.setBaseImageId("imgA");
        task.setTargetImageId("imgB");
        task.setResultPath("D:/tmp/task-1_overlay.png");

        Mockito.when(taskService.get(anyString())).thenReturn(task);

        mockMvc.perform(get("/api/task/task-1/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.id").value("task-1"))
                .andExpect(jsonPath("$.data.status").value("success"))
                .andExpect(jsonPath("$.data.progress").value(100));
    }
}

