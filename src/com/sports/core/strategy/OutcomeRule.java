package com.sports.core.strategy;

import com.sports.core.contest.Contest;
import com.sports.core.contest.Result;

public interface OutcomeRule {
    Result buildResult(Contest contest);
}
