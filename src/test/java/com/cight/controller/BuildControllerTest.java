package com.cight.controller;


import com.cight.dto.BuildEventRequest;
import com.cight.exception.BuildEventNotFoundException;
import com.cight.model.BuildEvent;
import com.cight.model.BuildStatus;
import com.cight.service.BuildEventService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BuildController.class)
public class BuildControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BuildEventService buildEventService;

    @Test
    public void createBuildEvent_whenRequestIsValid_shouldReturn201AndCallService() throws Exception {
        String validRequest= """
               {
               "repoName": "test-repo",
               "branch": "test-branch",
               "status": "SUCCESS",
               "commitSha" : "abc123",
               "errorLog" : "No error",
               "duration": 120
               }
               """;

        when(buildEventService.saveBuildEvent(any(BuildEventRequest.class)))
                .thenReturn(buildEvent());

        mockMvc.perform(
                post("/api/builds")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequest)
        )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("build-1"))
                .andExpect(jsonPath("$.repoName").value("test-repo"))
                .andExpect(jsonPath("$.branch").value("test-branch"))
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.commitSha").value("abc123"))
                .andExpect(jsonPath("$.duration").value(120))
                .andExpect(jsonPath("$.createdAt").value("2026-10-05T12:30:00"))
                .andExpect(jsonPath("$.errorLog").doesNotExist());
        verify(buildEventService)
                .saveBuildEvent(argThat(request -> request.status() == BuildStatus.SUCCESS));
    }


    @Test
    public void createBuildEvent_whenRepoNameIsBlank_shouldReturn400AndNotCallService()
            throws Exception {

        String invalidRequest = """
                {
                  "repoName": "",
                  "branch": "test-branch",
                  "status": "SUCCESS",
                  "commitSha": "abc123",
                  "errorLog": "No error",
                  "duration": 120
                }
                """;

        mockMvc.perform(
                        post("/api/builds")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(invalidRequest)
                )
                .andExpect(status().isBadRequest());

        verify(buildEventService, never())
                .saveBuildEvent(any(BuildEventRequest.class));
    }

    @Test
    public void createBuildEvent_whenDurationIsNegative_shouldReturn400AndNotCallService()
            throws Exception {

        String invalidRequest = """
                {
                  "repoName": "test-repo",
                  "branch": "test-branch",
                  "status": "FAILURE",
                  "commitSha": "abc123",
                  "errorLog": "Build failed",
                  "duration": -1
                }
                """;

        mockMvc.perform(
                        post("/api/builds")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(invalidRequest)
                )
                .andExpect(status().isBadRequest());

        verify(buildEventService, never())
                .saveBuildEvent(any(BuildEventRequest.class));
    }

    @Test
    public void createBuildEvent_whenBranchIsBlank_shouldReturn400AndNotCallService()
            throws Exception {

        String invalidRequest = """
                {
                  "repoName": "test-repo",
                  "branch": "",
                  "status": "FAILURE",
                  "commitSha": "abc123",
                  "errorLog": "Build failed",
                  "duration": 120
                }
                """;

        mockMvc.perform(
                        post("/api/builds")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(invalidRequest)
                )
                .andExpect(status().isBadRequest());

        verify(buildEventService, never())
                .saveBuildEvent(any(BuildEventRequest.class));
    }

    @Test
    public void createBuildEvent_whenStatusIsBlank_shouldReturn400AndNotCallService()
            throws Exception {

        String invalidRequest = """
                {
                  "repoName": "test-repo",
                  "branch": "test-branch",
                  "status": "",
                  "commitSha": "abc123",
                  "errorLog": "Build failed",
                  "duration": 120
                }
                """;

        mockMvc.perform(
                        post("/api/builds")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(invalidRequest)
                )
                .andExpect(status().isBadRequest());

        verify(buildEventService, never())
                .saveBuildEvent(any(BuildEventRequest.class));
    }

    @Test
    public void createBuildEvent_whenStatusIsUnsupported_shouldReturnProblemAndNotCallService()
            throws Exception {
        String invalidRequest = """
                {
                  "repoName": "test-repo",
                  "branch": "test-branch",
                  "status": "BROKEN"
                }
                """;

        mockMvc.perform(post("/api/builds")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.type").value("urn:cight:problem:invalid-request"))
                .andExpect(jsonPath("$.title").value("Invalid request"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").value(
                        "The request body is invalid or contains an unsupported value."))
                .andExpect(jsonPath("$.instance").value("/api/builds"));

        verifyNoInteractions(buildEventService);
    }

    @Test
    public void getBuildById_whenFound_shouldReturnResponseWithoutErrorLog() throws Exception {
        when(buildEventService.getBuildEventById("build-1")).thenReturn(buildEvent());

        mockMvc.perform(get("/api/builds/build-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("build-1"))
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.errorLog").doesNotExist());

        verify(buildEventService).getBuildEventById("build-1");
    }

    @Test
    public void getBuildById_whenMissing_shouldReturnNotFoundProblem() throws Exception {
        when(buildEventService.getBuildEventById("missing"))
                .thenThrow(new BuildEventNotFoundException("missing"));

        mockMvc.perform(get("/api/builds/missing"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.type").value("urn:cight:problem:build-not-found"))
                .andExpect(jsonPath("$.title").value("Build not found"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.detail").value("The requested build does not exist."))
                .andExpect(jsonPath("$.instance").value("/api/builds/missing"));
    }

    @Test
    public void getBuildLists_shouldReturnResponseRecordsWithoutErrorLogs() throws Exception {
        when(buildEventService.getAllBuildEvents()).thenReturn(List.of(buildEvent()));
        when(buildEventService.getBuildEventsByRepo("test-repo"))
                .thenReturn(List.of(buildEvent()));

        mockMvc.perform(get("/api/builds"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("SUCCESS"))
                .andExpect(jsonPath("$[0].errorLog").doesNotExist());
        mockMvc.perform(get("/api/builds/repo/test-repo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("SUCCESS"))
                .andExpect(jsonPath("$[0].errorLog").doesNotExist());
    }

    private static BuildEvent buildEvent() {
        return BuildEvent.builder()
                .id("build-1")
                .repoName("test-repo")
                .branch("test-branch")
                .status(BuildStatus.SUCCESS)
                .commitSha("abc123")
                .errorLog("private failure evidence")
                .duration(120L)
                .createdAt(LocalDateTime.of(2026, 10, 5, 12, 30))
                .build();
    }
}
