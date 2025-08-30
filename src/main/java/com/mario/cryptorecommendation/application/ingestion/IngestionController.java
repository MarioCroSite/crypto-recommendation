package com.mario.cryptorecommendation.application.ingestion;


import com.mario.cryptorecommendation.domain.ingestion.IngestionService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/ingestion")
@RequiredArgsConstructor
@Validated
public class IngestionController {

    private final IngestionService ingestionService;

    @GetMapping("/start")
    public ResponseEntity<Void> startIngestion(@RequestParam("directory") @NotBlank String directoryLocation) {
        log.info("Received request to start ingestion for directory location: {}", directoryLocation);
        ingestionService.startIngestion(directoryLocation);
        return ResponseEntity.ok().build();
    }

}
