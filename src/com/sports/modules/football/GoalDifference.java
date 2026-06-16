package com.sports.modules.football;

import com.sports.core.contest.Contest;
import com.sports.core.entity.Participant;
import com.sports.core.strategy.Tiebreak;
import java.util.*;

/** Tiebreak: highest goal difference (goals scored − goals conceded) across all decided contests. */
public class GoalDifference implements Tiebreak {
    private final Tiebreak next;

    public GoalDifference(Tiebreak next) { this.next = next; }

    @Override
    public List<Participant> resolve(List<Participant> tied, List<Contest> decidedContests) {
        throw new UnsupportedOperationException("TODO");
    }
}
