package com.sports.modules.chess;

import com.sports.core.contest.Contest;
import com.sports.core.contest.TerminationKind;
import com.sports.core.strategy.TerminationRule;
import java.util.List;

/**
 * Chess termination: inspect the last MoveEvent for a non-NOT_OVER TerminationKind.
 * The rule never says "who won?" — only "is it over, and how?".
 */
public class TerminalCondition implements TerminationRule {

    @Override
    public TerminationKind isOver(Contest contest) {
        throw new UnsupportedOperationException("TODO");
    }
}
