package com.mario.cryptorecommendation.application.ingestion;

import com.mario.cryptorecommendation.domain.ingestion.IngestionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.Stream;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(IngestionController.class)
class IngestionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IngestionService ingestionService;

    private static final String DIRECTORY_LOCATION = "src/test/resources/statements";

    @Test
    @WithMockUser(username = "admin", password = "admin")
    void shouldStartIngestionSuccessfully() throws Exception {
        // Given
        doNothing().when(ingestionService).startIngestion(DIRECTORY_LOCATION);

        // When & Then
        mockMvc.perform(get("/api/v1/ingestion/start")
                        .param("directory", DIRECTORY_LOCATION)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verify(ingestionService, times(1)).startIngestion(DIRECTORY_LOCATION);
    }

    @Test
    @WithMockUser(username = "admin", password = "admin")
    void shouldReturnBadRequestWhenDirectoryIsMissing() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/ingestion/start")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t", "\n"})
    @WithMockUser(username = "admin", password = "admin")
    void shouldReturnBadRequestForInvalidDirectoryValues(String invalidDirectory) throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/ingestion/start")
                        .param("directory", invalidDirectory)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnUnauthorizedWhenNotAuthenticated() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/ingestion/start")
                        .param("directory", DIRECTORY_LOCATION)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @ParameterizedTest
    @MethodSource("provideServiceExceptionScenarios")
    @WithMockUser(username = "admin", password = "admin")
    void shouldHandleServiceExceptions(Exception exception) throws Exception {
        // Given
        doThrow(exception).when(ingestionService).startIngestion(DIRECTORY_LOCATION);

        // When & Then
        mockMvc.perform(get("/api/v1/ingestion/start")
                        .param("directory", DIRECTORY_LOCATION)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    private static Stream<Arguments> provideServiceExceptionScenarios() {
        return Stream.of(
                Arguments.of(new RuntimeException("Service error")),
                Arguments.of(new IllegalArgumentException("Invalid directory")),
                Arguments.of(new IllegalStateException("Service unavailable"))
        );
    }
}
