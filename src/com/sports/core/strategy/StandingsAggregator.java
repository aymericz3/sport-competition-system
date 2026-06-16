package com.sports.core.strategy;

import com.sports.core.competition.Leaderboard;
import com.sports.core.contest.Contest;
import java.util.List;

public interface StandingsAggregator {
    Leaderboard compute(List<Contest> decidedContests,
                        ScoringRule scoringRule,
                        List<Tiebreak> tiebreaks);
}
