package com.nefu.practice.controller;

import com.nefu.practice.entity.Image;
import com.nefu.practice.serivce.ImageService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ImageController.class)
class ImageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ImageService imageService;

    @Test
    void uploadImage_shouldReturnImageId() throws Exception {
        Mockito.when(imageService.upload(any(), any(), any())).thenReturn("img-1");

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "a.bmp",
                MediaType.IMAGE_JPEG_VALUE,
                "dummy".getBytes()
        );

        mockMvc.perform(multipart("/api/image/upload")
                        .file(file)
                        .param("name", "n1")
                        .param("remark", "r1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.imageId").value("img-1"));
    }

    @Test
    void list_shouldReturnImages() throws Exception {
        Image img = new Image();
        img.setId("img-1");
        img.setName("n1");
        img.setFilePath("D:/tmp/a.bmp");
        img.setUploadTime(LocalDateTime.of(2025, 12, 27, 10, 0));
        img.setRemark("r1");

        Mockito.when(imageService.list()).thenReturn(List.of(img));

        mockMvc.perform(get("/api/image/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data[0].id").value("img-1"))
                .andExpect(jsonPath("$.data[0].name").value("n1"))
                .andExpect(jsonPath("$.data[0].remark").value("r1"))
                .andExpect(jsonPath("$.data[0].uploadTime").exists());
    }
}

