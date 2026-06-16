package com.sports.modules.football;

import com.sports.core.contest.*;
import com.sports.core.entity.Participant;
import com.sports.core.strategy.Tiebreak;
import com.sports.modules.common.WinDrawLoss;
import java.util.*;

/** Tiebreak: head-to-head points (WDL 3/1/0) among only the tied participants. */
public class HeadToHead implements Tiebreak {
    private final Tiebreak next;

    public HeadToHead(Tiebreak next) { this.next = next; }

    @Override
    public List<Participant> resolve(List<Participant> tied, List<Contest> decidedContests) {
        throw new UnsupportedOperationException("TODO");
    }
}
