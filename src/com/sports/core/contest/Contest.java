package com.sports.core.contest;

import com.sports.core.strategy.OutcomeRule;
import com.sports.core.strategy.TerminationRule;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * A self-contained encounter that produces exactly one Result once decided.
 *
 * State machine (normal path): SCHEDULED → IN_PROGRESS → FINISHED
 * Pause branch:                IN_PROGRESS ↔ SUSPENDED
 * Exception endings:           SCHEDULED|IN_PROGRESS → FORFEITED | WALKOVER | ABANDONED | CANCELLED_VOID
 * Correction only:             FINISHED → REOPENED → IN_PROGRESS  (explicit, audited)
 *
 * Invariants to enforce:
 *  - at least one Participation must exist before start()
 *  - a participant appears at most once (enforced by callers / FixtureGenerator)
 *  - Result exists iff the contest is in a decided state
 *  - the state never moves backward without going through REOPENED
 */
public class Contest {
    private final String id;
    private final List<Participation> participations;
    private final TerminationRule terminationRule;
    private final OutcomeRule outcomeRule;
    private final List<Object> eventLog;
    private final List<Contest> children; // composite — for nested contests (e.g. tennis sets)

    private ContestState state;
    private Result result;
    private TerminationKind terminationKind;
    private int elapsedMinutes;

    public Contest(List<Participation> participations,
                   TerminationRule terminationRule,
                   OutcomeRule outcomeRule) {
        if (participations == null || participations.isEmpty())
            throw new IllegalArgumentException("A contest needs at least one participation");
        this.id = UUID.randomUUID().toString();
        this.participations = new ArrayList<>(participations);
        this.terminationRule = terminationRule;
        this.outcomeRule = outcomeRule;
        this.eventLog = new ArrayList<>();
        this.children = new ArrayList<>();
        this.state = ContestState.SCHEDULED;
    }

    // ── Queries ──────────────────────────────────────────────────────────────

    public String getId()                       { return id; }
    public ContestState getState()              { return state; }
    public List<Participation> getParticipations() { return Collections.unmodifiableList(participations); }
    public Result getResult()                   { return result; }
    public TerminationKind getTerminationKind() { return terminationKind; }
    public int getElapsedMinutes()              { return elapsedMinutes; }
    public List<Object> getEventLog()           { return Collections.unmodifiableList(eventLog); }
    public List<Contest> getChildren()          { return Collections.unmodifiableList(children); }
    public TerminationRule getTerminationRule() { return terminationRule; }
    public OutcomeRule getOutcomeRule()         { return outcomeRule; }

    public boolean isDecided() {
        throw new UnsupportedOperationException("TODO");
    }

    // ── State transitions ─────────────────────────────────────────────────────

    public void start() {
        throw new UnsupportedOperationException("TODO");
    }

    public void suspend() {
        throw new UnsupportedOperationException("TODO");
    }

    public void resume() {
        throw new UnsupportedOperationException("TODO");
    }

    public void cancel() {
        throw new UnsupportedOperationException("TODO");
    }

    public void reopen() {
        throw new UnsupportedOperationException("TODO");
    }

    public void resumeCorrection() {
        throw new UnsupportedOperationException("TODO");
    }

    /** Record a domain event (e.g. GoalEvent, MoveEvent) and check termination. */
    public void recordEvent(Object event) {
        throw new UnsupportedOperationException("TODO");
    }

    /** Advance elapsed time for time-limited disciplines and check termination. */
    public void setElapsedMinutes(int minutes) {
        throw new UnsupportedOperationException("TODO");
    }

    /** Forfeit: loser failed to complete; winner takes the points. */
    public void forfeit(Participation loser) {
        throw new UnsupportedOperationException("TODO");
    }

    /** Walkover: absentee never showed up. */
    public void walkover(Participation absentee) {
        throw new UnsupportedOperationException("TODO");
    }

    /** Abandon — discipline decides separately whether this result stands. */
    public void abandon() {
        throw new UnsupportedOperationException("TODO");
    }

    /** Mark an abandoned contest as decided (discipline rules it stands). */
    public void acceptAbandonedResult() {
        throw new UnsupportedOperationException("TODO");
    }

    /** Add a nested contest (e.g. a tennis set inside a match). */
    public void addChild(Contest child) {
        throw new UnsupportedOperationException("TODO");
    }

    // ── Internal ─────────────────────────────────────────────────────────────

    /** Asks the TerminationRule whether the contest is over; if so, finalises it. */
    public void checkTermination() {
        throw new UnsupportedOperationException("TODO");
    }

    private void finalise(TerminationKind kind) {
        throw new UnsupportedOperationException("TODO");
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new IllegalStateException(message);
    }

    @Override
    public String toString() {
        return "Contest[" + id.substring(0, 6) + ", " + state + "]";
    }
}
