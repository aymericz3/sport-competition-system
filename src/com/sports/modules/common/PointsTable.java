package com.sports.modules.common;

import com.sports.core.competition.Leaderboard;
import com.sports.core.contest.Contest;
import com.sports.core.contest.Result;
import com.sports.core.contest.StandingContribution;
import com.sports.core.entity.Participant;
import com.sports.core.strategy.ScoringRule;
import com.sports.core.strategy.StandingsAggregator;
import com.sports.core.strategy.Tiebreak;
import java.util.*;

/**
 * Sums points from every decided contest, then applies the Tiebreak chain on equal groups.
 * Used by football leagues and chess Swiss tournaments.
 * Always recomputed from scratch — never cached.
 */
public class PointsTable implements StandingsAggregator {

    @Override
    public Leaderboard compute(List<Contest> decidedContests,
                               ScoringRule scoringRule,
                               List<Tiebreak> tiebreaks) {
        throw new UnsupportedOperationException("TODO");
    }
}
