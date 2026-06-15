package com.sports.core.strategy;

import com.sports.core.contest.Contest;
import com.sports.core.entity.Participant;
import java.util.List;

public interface Tiebreak {
    List<Participant> resolve(List<Participant> tied, List<Contest> decidedContests);
}
