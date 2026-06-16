package com.sports.modules.chess;

import com.sports.core.contest.*;
import com.sports.core.strategy.OutcomeRule;
import java.util.*;

/**
 * Chess outcome rule.
 *
 * For draws the result is symmetric. For decisive endings:
 *  - CHECKMATE: the player who delivered it (last MoveEvent author) wins.
 *  - RESIGNATION: the player who recorded the resignation (last MoveEvent author) loses.
 *  - FORFEIT / WALKOVER: the participant whose status is DID_NOT_FINISH / DID_NOT_START loses.
 */
public class Declared implements OutcomeRule {

    @Override
    public Result buildResult(Contest contest) {
        throw new UnsupportedOperationException("TODO");
    }
}
