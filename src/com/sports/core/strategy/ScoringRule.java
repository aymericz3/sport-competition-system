package com.sports.core.strategy;

import com.sports.core.contest.StandingContribution;

public interface ScoringRule {
    double points(StandingContribution contribution);
}
