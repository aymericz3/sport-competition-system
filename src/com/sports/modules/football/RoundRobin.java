package com.sports.modules.football;

import com.sports.core.contest.Contest;
import com.sports.core.contest.Participation;
import com.sports.core.entity.Participant;
import com.sports.core.strategy.FixtureGenerator;
import com.sports.core.strategy.OutcomeRule;
import com.sports.core.strategy.ScoringRule;
import com.sports.core.strategy.TerminationRule;
import java.util.ArrayList;
import java.util.List;

/** Every participant meets every other once (or twice if homeAndAway). Emits the full schedule upfront. */
public class RoundRobin implements FixtureGenerator {
    private final boolean homeAndAway;

    public RoundRobin(boolean homeAndAway) { this.homeAndAway = homeAndAway; }

    @Override public boolean isAdaptive() { return false; }

    @Override
    public List<Contest> generateAll(List<Participant> entrants,
                                     TerminationRule tRule, OutcomeRule oRule) {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public List<Contest> nextRound(List<Participant> entrants, List<Contest> decided,
                                   ScoringRule sr, TerminationRule tRule, OutcomeRule oRule) {
        throw new UnsupportedOperationException("TODO");
    }
}
