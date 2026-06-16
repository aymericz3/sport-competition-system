package com.sports.core.strategy;

import com.sports.core.contest.Contest;
import com.sports.core.contest.TerminationKind;

public interface TerminationRule {
    TerminationKind isOver(Contest contest);
}
