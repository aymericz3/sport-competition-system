package com.sports.modules.common;

import com.sports.core.contest.StandingContribution;
import com.sports.core.strategy.ScoringRule;

/** Converts a "won"/"drew"/"lost" token into points. Used by football (3,1,0) and chess (1,0.5,0). */
public class WinDrawLoss implements ScoringRule {
    private final double winPoints;
    private final double drawPoints;
    private final double lossPoints;

    public WinDrawLoss(double win, double draw, double loss) {
        this.winPoints = win;
        this.drawPoints = draw;
        this.lossPoints = loss;
    }

    @Override
    public double points(StandingContribution contribution) {
        throw new UnsupportedOperationException("TODO");
    }
}
