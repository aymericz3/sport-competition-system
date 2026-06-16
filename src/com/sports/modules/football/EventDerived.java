package com.sports.modules.football;

import com.sports.core.contest.*;
import com.sports.core.strategy.OutcomeRule;
import java.util.*;

public class EventDerived implements OutcomeRule {

    @Override
    public Result buildResult(Contest contest) {
        Map<Participation, Integer> goals = new LinkedHashMap<>();

        for (Participation participation : contest.getParticipations()) {
            goals.put(participation, 0);
        }

        for (Object event : contest.getEventLog()) {
            if (event instanceof GoalEvent) {
                GoalEvent goal = (GoalEvent) event;
                Participation scorer = goal.getScorer();

                if (goals.containsKey(scorer)) {
                    goals.put(scorer, goals.get(scorer) + 1);
                }
            }
        }

        for (Participation participation : contest.getParticipations()) {
            participation.complete(String.valueOf(goals.get(participation)));
        }

        List<Participation> ranked = new ArrayList<>(contest.getParticipations());
        ranked.sort((a, b) -> Integer.compare(goals.get(b), goals.get(a)));

        Map<Participation, StandingContribution> contributions = new LinkedHashMap<>();

        if (ranked.size() == 2 && Objects.equals(goals.get(ranked.get(0)), goals.get(ranked.get(1)))) {
            contributions.put(ranked.get(0), StandingContribution.drew());
            contributions.put(ranked.get(1), StandingContribution.drew());
        } else {
            for (int i = 0; i < ranked.size(); i++) {
                Participation participation = ranked.get(i);

                if (i == 0) {
                    contributions.put(participation, StandingContribution.won());
                } else {
                    contributions.put(participation, StandingContribution.lost());
                }
            }
        }

        return new SimpleResult(ranked, contributions);
    }
}