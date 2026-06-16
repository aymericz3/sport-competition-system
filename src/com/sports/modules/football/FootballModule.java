package com.sports.modules.football;

import com.sports.core.competition.Competition;
import com.sports.core.entity.Participant;
import com.sports.core.entity.Sport;
import com.sports.core.strategy.*;
import com.sports.modules.common.PointsTable;
import com.sports.modules.common.WinDrawLoss;
import java.util.Arrays;
import java.util.List;

/** Factory that wires the football strategy bundle into a Competition. */
public class FootballModule {

    public static final Sport SPORT = new Sport(
        "Football",
        Arrays.asList("HOME", "AWAY"),
        true,   // authoritative event log (goals)
        true    // participants have rosters
    );

    public static Competition league(String name, List<Participant> entrants) {
        throw new UnsupportedOperationException("TODO");
    }
}
