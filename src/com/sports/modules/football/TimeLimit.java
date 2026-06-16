package com.sports.modules.football;

import com.sports.core.contest.Contest;
import com.sports.core.contest.TerminationKind;
import com.sports.core.strategy.TerminationRule;

/** Ends a football match when elapsed minutes reach the configured limit. */
public class TimeLimit implements TerminationRule {
    private final int regularMinutes;

    public TimeLimit(int regularMinutes) {
        this.regularMinutes = regularMinutes;
    }

    @Override
    public TerminationKind isOver(Contest contest) {
        throw new UnsupportedOperationException("TODO");
    }
}
