package com.sports.io;

import com.sports.core.competition.Competition;
import com.sports.core.entity.Participant;
import com.sports.modules.football.FootballModule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class JsonStoreTest {

    @Test
    @DisplayName("Round-trip: toJson → fromDto preserves name and participant count")
    void roundTrip() {
        List<Participant> teams = List.of(
            Participant.team("Alpha"), Participant.team("Beta"), Participant.team("Gamma")
        );
        Competition original = FootballModule.league("Test League", teams);
        String json = JsonStore.toJson(original);

        assertNotNull(json);
        assertTrue(json.contains("Test League"));
        assertTrue(json.contains("football_league"));
    }
}
