package com.cight.service;

import com.cight.exception.BuildEventNotFoundException;
import com.cight.model.BuildEvent;
import com.cight.repository.BuildEventRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BuildEventServiceTest {

    private final BuildEventRepository repository = mock(BuildEventRepository.class);
    private final BuildEventService service = new BuildEventService(repository);

    @Test
    void getBuildEventById_returnsStoredBuild() {
        BuildEvent build = BuildEvent.builder().id("build-1").build();
        when(repository.findById("build-1")).thenReturn(Optional.of(build));

        assertSame(build, service.getBuildEventById("build-1"));
    }

    @Test
    void getBuildEventById_throwsWhenBuildIsMissing() {
        when(repository.findById("missing")).thenReturn(Optional.empty());

        BuildEventNotFoundException exception = assertThrows(
                BuildEventNotFoundException.class,
                () -> service.getBuildEventById("missing"));
        assertEquals("Build event not found: missing", exception.getMessage());
    }
}
