package com.sports.core.competition;

import com.sports.core.contest.Contest;
import com.sports.core.entity.Participant;
import com.sports.core.entity.Sport;
import com.sports.core.strategy.*;
import java.util.*;

/**
 * A group of participants playing under a Format, standings derived by a ScoringRule.
 *
 * Invariants:
 *  - every Contest uses only this competition's entrants
 *  - computeStandings() is always recomputed from decided results — never cached
 *  - ScoringRule lives here, not on Sport, so the same sport may score differently elsewhere
 */
public class Competition {
    private final String id;
    private final String name;
    private final Sport sport;
    private final List<Participant> entrants;
    private final List<Contest> contests;
    private final ScoringRule scoringRule;
    private final FixtureGenerator fixtureGenerator;
    private final StandingsAggregator standingsAggregator;
    private final List<Tiebreak> tiebreaks;
    private final TerminationRule cachedTRule;
    private final OutcomeRule cachedORule;

    public Competition(String name,
                       Sport sport,
                       List<Participant> entrants,
                       ScoringRule scoringRule,
                       FixtureGenerator fixtureGenerator,
                       StandingsAggregator standingsAggregator,
                       List<Tiebreak> tiebreaks,
                       TerminationRule tRule,
                       OutcomeRule oRule) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.sport = sport;
        this.entrants = new ArrayList<>(entrants);
        this.contests = new ArrayList<>();
        this.scoringRule = scoringRule;
        this.fixtureGenerator = fixtureGenerator;
        this.standingsAggregator = standingsAggregator;
        this.tiebreaks = new ArrayList<>(tiebreaks);
        this.cachedTRule = tRule;
        this.cachedORule = oRule;

        // TODO: validate entrants, then generate initial fixtures
        // (non-adaptive: generateAll; adaptive: nextRound with no prior results)
    }

    // ── Queries ───────────────────────────────────────────────────────────────

    public String getId()                       { return id; }
    public String getName()                     { return name; }
    public Sport getSport()                     { return sport; }
    public List<Participant> getEntrants()      { return Collections.unmodifiableList(entrants); }
    public List<Contest> getContests()          { return Collections.unmodifiableList(contests); }
    public ScoringRule getScoringRule()         { return scoringRule; }
    public FixtureGenerator getFixtureGenerator() { return fixtureGenerator; }
    public List<Tiebreak> getTiebreaks()        { return Collections.unmodifiableList(tiebreaks); }

    public List<Contest> getDecidedContests() {
        List<Contest> decided = new ArrayList<>();
        for (Contest c : contests) if (c.isDecided()) decided.add(c);
        return decided;
    }

    /** Always recomputed — no stored Stats classes. */
    public Leaderboard computeStandings() {
        throw new UnsupportedOperationException("TODO");
    }

    // ── Mutations ─────────────────────────────────────────────────────────────

    /** Generate next round for adaptive formats (Swiss, knockout). */
    public void generateNextRound() {
        throw new UnsupportedOperationException("TODO");
    }

    /** Add a manually-created contest (e.g. a re-scheduled fixture). */
    public void addContest(Contest contest) { contests.add(contest); }

    @Override public String toString() { return name; }
}
