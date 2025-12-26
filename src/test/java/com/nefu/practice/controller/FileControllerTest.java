package com.nefu.practice.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = FileController.class)
@TestPropertySource(properties = {
        "file.result-dir=${java.io.tmpdir}"
})
class FileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getResult_overlayShouldFallbackToMask_whenOverlayMissing() throws Exception {
        String taskId = "t1";

        // 创建一个 mask 文件：t1_mask.bmp
        Path bmp = Path.of(System.getProperty("java.io.tmpdir"), taskId + "_mask.bmp");
        Files.write(bmp, new byte[] { 0 });

        try {
            mockMvc.perform(get("/api/file/result/" + taskId))
                    .andExpect(status().isOk());
        } finally {
            Files.deleteIfExists(bmp);
        }
    }

    @Test
    void getResultByType_maskNotFound_shouldReturn404() throws Exception {
        mockMvc.perform(get("/api/file/result/not-exist/mask"))
                .andExpect(status().isNotFound());
    }
}

