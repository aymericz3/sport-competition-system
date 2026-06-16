package com.sports.modules.chess;

import com.sports.core.contest.Contest;
import com.sports.core.contest.Participation;
import com.sports.core.entity.Participant;
import com.sports.core.strategy.ScoringRule;
import com.sports.core.strategy.Tiebreak;
import java.util.*;

/** Secondary tiebreak: sum of (score vs each opponent × that opponent's total score). */
public class SonnebornBerger implements Tiebreak {
    private final ScoringRule scoringRule;

    public SonnebornBerger(ScoringRule scoringRule) { this.scoringRule = scoringRule; }

    @Override
    public List<Participant> resolve(List<Participant> tied, List<Contest> decidedContests) {
        throw new UnsupportedOperationException("TODO");
    }
}
