package com.cight.build;

import com.cight.shared.ApiExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class BuildControllerTest {

    MockMvc mockMvc;
    BuildEventService service;

    @BeforeEach
    void setUp() {
        service = mock(BuildEventService.class);
        mockMvc = MockMvcBuilders
                .standaloneSetup(new BuildController(service))
                .setControllerAdvice(new ApiExceptionHandler())
                .build();
    }

    @Test
    void rejectsBlankRequiredFields() throws Exception {
        mockMvc.perform(post("/api/builds")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "repoName": " ",
                                  "branch": "",
                                  "status": "FAILURE",
                                  "durationMs": -1
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation failed"))
                .andExpect(jsonPath("$.errors.repoName").exists())
                .andExpect(jsonPath("$.errors.branch").exists())
                .andExpect(jsonPath("$.errors.durationMs").exists());
    }
}
