package com.sports.modules.football;

import com.sports.core.contest.*;
import com.sports.core.strategy.OutcomeRule;
import java.util.*;

/**
 * Counts GoalEvents to produce a football Result.
 * Handles forfeit/walkover: the participant whose status is DID_NOT_FINISH or DID_NOT_START loses.
 */
public class EventDerived implements OutcomeRule {

    @Override
    public Result buildResult(Contest contest) {
        throw new UnsupportedOperationException("TODO");
    }
}
