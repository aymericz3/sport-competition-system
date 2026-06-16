package com.sports.core.strategy;

import com.sports.core.contest.Contest;
import com.sports.core.entity.Participant;
import java.util.List;

public interface FixtureGenerator {
    boolean isAdaptive();

    List<Contest> generateAll(List<Participant> entrants,
                              TerminationRule tRule,
                              OutcomeRule oRule);

    List<Contest> nextRound(List<Participant> entrants,
                            List<Contest> decidedContests,
                            ScoringRule scoringRule,
                            TerminationRule tRule,
                            OutcomeRule oRule);
}
