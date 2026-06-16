package com.sports.core.contest;

import com.sports.core.entity.Participant;
import java.util.UUID;

public class Participation {
    private final String id;
    private final Participant participant;
    private final String role;
    private ParticipationStatus status;
    private String outcomeValue;

    public Participation(Participant participant, String role) {
        this.id = UUID.randomUUID().toString();
        this.participant = participant;
        this.role = role;
        this.status = ParticipationStatus.ENTERED;
    }

    public String getId()                  { return id; }
    public Participant getParticipant()    { return participant; }
    public String getRole()                { return role; }
    public ParticipationStatus getStatus() { return status; }
    public String getOutcomeValue()        { return outcomeValue; }

    public void activate()                 { throw new UnsupportedOperationException("TODO"); }
    public void complete(String value)     { throw new UnsupportedOperationException("TODO"); }
    public void didNotFinish()             { throw new UnsupportedOperationException("TODO"); }
    public void didNotStart()              { throw new UnsupportedOperationException("TODO"); }
    public void disqualify()               { throw new UnsupportedOperationException("TODO"); }
    public void withdraw()                 { throw new UnsupportedOperationException("TODO"); }

    @Override
    public String toString() {
        return participant.getName() + (role != null ? "[" + role + "]" : "");
    }
}
