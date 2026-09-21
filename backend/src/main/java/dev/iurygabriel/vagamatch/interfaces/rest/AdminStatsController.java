package dev.iurygabriel.vagamatch.interfaces.rest;

import dev.iurygabriel.vagamatch.admin.application.GetAdminStatsUseCase;
import dev.iurygabriel.vagamatch.admin.application.GetAdminStatsUseCase.AdminStats;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/** Estatísticas do painel administrativo (somente ADMIN). */
@RestController
public class AdminStatsController {

    private final GetAdminStatsUseCase getAdminStats;

    public AdminStatsController(GetAdminStatsUseCase getAdminStats) {
        this.getAdminStats = getAdminStats;
    }

    @GetMapping("/api/admin/stats")
    public AdminStats stats() {
        return getAdminStats.execute();
    }
}
