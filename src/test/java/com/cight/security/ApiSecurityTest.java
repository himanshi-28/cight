package com.cight.security;

import com.cight.build.BuildController;
import com.cight.build.BuildEventPageResponse;
import com.cight.build.BuildEventService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BuildController.class)
@Import(SecurityConfig.class)
@TestPropertySource(properties = "cight.security.enabled=true")
class ApiSecurityTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    BuildEventService service;

    @MockitoBean
    JwtDecoder jwtDecoder;

    @MockitoBean
    CacheManager cacheManager;

    @Test
    void rejectsMissingToken() throws Exception {
        mockMvc.perform(get("/api/builds")).andExpect(status().isUnauthorized());
    }

    @Test
    void rejectsInsufficientScope() throws Exception {
        mockMvc.perform(get("/api/builds")
                        .with(jwt().authorities(
                                new SimpleGrantedAuthority("SCOPE_builds:write")
                        )))
                .andExpect(status().isForbidden());
    }

    @Test
    void acceptsReadScope() throws Exception {
        when(service.find(isNull(), isNull(), anyInt(), anyInt()))
                .thenReturn(new BuildEventPageResponse(List.of(), 0, 20, 0, 0));

        mockMvc.perform(get("/api/builds")
                        .with(jwt().authorities(
                                new SimpleGrantedAuthority("SCOPE_builds:read")
                        )))
                .andExpect(status().isOk());
    }
}
