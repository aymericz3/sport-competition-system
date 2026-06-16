package com.sports.modules.chess;

import com.sports.core.competition.Competition;
import com.sports.core.entity.Participant;
import com.sports.core.entity.Sport;
import com.sports.core.strategy.*;
import com.sports.modules.common.PointsTable;
import com.sports.modules.common.WinDrawLoss;
import java.util.Arrays;
import java.util.List;

/** Factory that wires the chess strategy bundle into a Competition. */
public class ChessModule {

    public static final Sport SPORT = new Sport(
        "Chess",
        Arrays.asList("WHITE", "BLACK"),
        true,   // move log is recorded
        false   // individual players
    );

    public static Competition swiss(String name, List<Participant> entrants) {
        throw new UnsupportedOperationException("TODO");
    }
}
