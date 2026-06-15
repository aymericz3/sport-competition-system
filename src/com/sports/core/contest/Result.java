package com.sports.core.contest;

import java.util.List;

public interface Result {
    List<Participation> getRankedParticipations();
    StandingContribution getContribution(Participation participation);
}
