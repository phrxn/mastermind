package com.quazzom.mastermind.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.quazzom.mastermind.dto.GameFullResponse;
import com.quazzom.mastermind.dto.RankingGlobalResponse;
import com.quazzom.mastermind.service.RankingService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Ranking Global", description = "Endpoints para ver o ranking global e detalhes de jogos específicos que estão no top 10 de cada level do ranking")
@SecurityRequirement(name = "bearerAuth")
public class RankingController {

    private final RankingService rankingService;

    public RankingController(RankingService rankingService) {
        this.rankingService = rankingService;
    }

    @GetMapping("/ranking")
    public ResponseEntity<RankingGlobalResponse> getGlobalRanking() {
        return ResponseEntity.ok(rankingService.getGlobalRanking());
    }

    @GetMapping("/ranking/{gameUuid}")
    public ResponseEntity<GameFullResponse> getGameRankingByUuid(@PathVariable UUID gameUuid) {
        return ResponseEntity.ok(rankingService.getRankedGameByUuidPublic(gameUuid));
    }
}
