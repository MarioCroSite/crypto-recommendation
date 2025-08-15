package com.mario.cryptorecommendation.application.recommendation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/recommendation")
@RequiredArgsConstructor
public class RecommendationController {


    @GetMapping("/normalized-range")
    public ResponseEntity<List<CryptoStatsRs>> getAllCryptosNormalizedRange() {

    }


    @GetMapping("/normalized-range/highest")
    public ResponseEntity<String> getCryptoWithHighestNormalizedRangeInDay(@RequestParam String date) {

    }

    @GetMapping("/stats/{symbol}/info")
    public ResponseEntity<CryptoStatsRs> getCryptoStats(@PathVariable String symbol) {

    }

}
