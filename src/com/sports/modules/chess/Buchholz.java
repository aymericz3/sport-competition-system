package com.sports.modules.chess;

import com.sports.core.contest.Contest;
import com.sports.core.contest.Participation;
import com.sports.core.entity.Participant;
import com.sports.core.strategy.ScoringRule;
import com.sports.core.strategy.Tiebreak;
import java.util.*;

/** Tiebreak: sum of opponents' total scores. Delegates remaining ties to next. */
public class Buchholz implements Tiebreak {
    private final ScoringRule scoringRule;
    private final Tiebreak next;

    public Buchholz(ScoringRule scoringRule, Tiebreak next) {
        this.scoringRule = scoringRule;
        this.next = next;
    }

    @Override
    public List<Participant> resolve(List<Participant> tied, List<Contest> decidedContests) {
        throw new UnsupportedOperationException("TODO");
    }
}
