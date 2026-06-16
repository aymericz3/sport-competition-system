package com.sports.io;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sports.core.competition.Competition;
import com.sports.core.contest.*;
import com.sports.core.entity.Member;
import com.sports.core.entity.Participant;
import com.sports.io.dto.*;
import com.sports.modules.chess.*;
import com.sports.modules.football.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Serialises and deserialises a Competition to/from JSON.
 * DTOs are kept deliberately separate from domain objects so persistence
 * concerns never leak into the core (this is exactly the CRUD-plumbing
 * layer the report kept out of the domain model).
 */
public class JsonStore {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    // ── File I/O ──────────────────────────────────────────────────────────────

    public static void save(Competition competition, String filePath) throws IOException {
        String json = GSON.toJson(toDto(competition));
        Files.writeString(Path.of(filePath), json);
    }

    public static Competition load(String filePath) throws IOException {
        String json = Files.readString(Path.of(filePath));
        return fromDto(GSON.fromJson(json, CompetitionDto.class));
    }

    public static String toJson(Competition competition) {
        return GSON.toJson(toDto(competition));
    }

    // ── Domain → DTO ──────────────────────────────────────────────────────────

    public static CompetitionDto toDto(Competition competition) {
        CompetitionDto dto = new CompetitionDto();
        dto.id   = competition.getId();
        dto.name = competition.getName();
        dto.type = typeOf(competition);

        dto.participants = new ArrayList<>();
        for (Participant p : competition.getEntrants()) dto.participants.add(participantDto(p));

        dto.contests = new ArrayList<>();
        for (Contest c : competition.getContests()) dto.contests.add(contestDto(c));
        return dto;
    }

    private static ParticipantDto participantDto(Participant p) {
        ParticipantDto dto = new ParticipantDto();
        dto.id     = p.getId();
        dto.name   = p.getName();
        dto.isTeam = p.isTeam();
        dto.members = new ArrayList<>();
        for (Member m : p.getRoster()) {
            MemberDto md = new MemberDto();
            md.id = m.getId(); md.name = m.getName();
            dto.members.add(md);
        }
        return dto;
    }

    private static ContestDto contestDto(Contest c) {
        ContestDto dto = new ContestDto();
        dto.id    = c.getId();
        dto.state = c.getState().name();
        dto.participations = new ArrayList<>();
        for (Participation p : c.getParticipations()) {
            ParticipationDto pd = new ParticipationDto();
            pd.participantId = p.getParticipant().getId();
            pd.role          = p.getRole();
            pd.status        = p.getStatus().name();
            pd.outcomeValue  = p.getOutcomeValue();
            dto.participations.add(pd);
        }
        dto.events = new ArrayList<>();
        for (Object ev : c.getEventLog()) {
            EventDto ed = eventDto(ev);
            if (ed != null) dto.events.add(ed);
        }
        return dto;
    }

    private static EventDto eventDto(Object ev) {
        if (ev instanceof GoalEvent g) {
            EventDto ed = new EventDto();
            ed.type       = "goal";
            ed.scorerRole = g.getScorer().getRole();
            ed.minute     = g.getMinute();
            return ed;
        }
        if (ev instanceof MoveEvent m) {
            EventDto ed = new EventDto();
            ed.type       = "move";
            ed.playerRole = m.getPlayer().getRole();
            ed.notation   = m.getNotation();
            ed.termination = m.getTerminationKind() == TerminationKind.NOT_OVER
                             ? null : m.getTerminationKind().name();
            return ed;
        }
        return null;
    }

    private static String typeOf(Competition comp) {
        if (comp.getSport().getName().equals("Football")) return "football_league";
        if (comp.getSport().getName().equals("Chess"))    return "chess_swiss";
        return "unknown";
    }

    // ── DTO → Domain ──────────────────────────────────────────────────────────

    public static Competition fromDto(CompetitionDto dto) {
        // Build participant index
        Map<String, Participant> byId = new LinkedHashMap<>();
        for (ParticipantDto pd : dto.participants) {
            Participant p = Participant.withId(pd.id, pd.name, pd.isTeam);
            if (pd.members != null) {
                for (MemberDto md : pd.members) p.addMember(new Member(md.id, md.name));
            }
            byId.put(pd.id, p);
        }
        List<Participant> entrants = new ArrayList<>(byId.values());

        // Create the competition skeleton (generates fixtures internally)
        Competition comp = switch (dto.type) {
            case "football_league" -> FootballModule.league(dto.name, entrants);
            case "chess_swiss"     -> ChessModule.swiss(dto.name, entrants);
            default -> throw new IllegalArgumentException("Unknown competition type: " + dto.type);
        };

        // Replay each persisted contest state onto the generated contests
        // Match by position (order is deterministic from FixtureGenerator)
        List<Contest> generated = comp.getContests();
        for (int i = 0; i < dto.contests.size() && i < generated.size(); i++) {
            replayContest(dto.contests.get(i), generated.get(i), byId);
        }
        return comp;
    }

    private static void replayContest(ContestDto dto, Contest contest,
                                      Map<String, Participant> byId) {
        ContestState targetState = ContestState.valueOf(dto.state);
        if (targetState == ContestState.SCHEDULED) return; // nothing to replay

        contest.start();

        // Build a role → Participation map for event replay
        Map<String, Participation> byRole = new LinkedHashMap<>();
        for (Participation p : contest.getParticipations()) byRole.put(p.getRole(), p);

        // Replay events
        if (dto.events != null) {
            for (EventDto ed : dto.events) {
                if ("goal".equals(ed.type)) {
                    Participation scorer = byRole.get(ed.scorerRole);
                    if (scorer != null) contest.getEventLog(); // read-only here; events added below
                }
            }
        }

        // For finished contests imported without a full event log, fast-path from outcomeValues
        if (targetState.isDecidedState() && (dto.events == null || dto.events.isEmpty())) {
            fastFinish(dto, contest, byRole, targetState);
            return;
        }

        // Replay events properly
        if (dto.events != null) {
            for (EventDto ed : dto.events) replayEvent(ed, contest, byRole);
        }

        // If the contest should be decided but wasn't reached via events, force-finish
        if (targetState.isDecidedState() && !contest.isDecided()) {
            fastFinish(dto, contest, byRole, targetState);
        }
    }

    private static void replayEvent(EventDto ed, Contest contest,
                                    Map<String, Participation> byRole) {
        switch (ed.type) {
            case "goal" -> {
                Participation scorer = byRole.get(ed.scorerRole);
                if (scorer != null) {
                    // Use a mutable-log trick: temporarily allow via recordEvent
                    try {
                        contest.recordEvent(new GoalEvent(scorer, ed.minute));
                    } catch (IllegalStateException ignored) { /* already finished */ }
                }
            }
            case "move" -> {
                Participation player = byRole.get(ed.playerRole);
                if (player != null) {
                    TerminationKind tk = ed.termination == null
                        ? TerminationKind.NOT_OVER
                        : TerminationKind.valueOf(ed.termination);
                    try {
                        contest.recordEvent(new MoveEvent(player, ed.notation, tk));
                    } catch (IllegalStateException ignored) { /* already finished */ }
                }
            }
        }
    }

    /**
     * When importing a FINISHED contest that has no event log (e.g. hand-authored seed data),
     * reconstruct the result directly from the participations' outcomeValues.
     */
    private static void fastFinish(ContestDto dto, Contest contest,
                                   Map<String, Participation> byRole,
                                   ContestState targetState) {
        // Set outcomeValues from DTO
        if (dto.participations != null) {
            for (ParticipationDto pd : dto.participations) {
                Participation p = byRole.get(pd.role);
                if (p != null && pd.outcomeValue != null) p.complete(pd.outcomeValue);
            }
        }
        // Determine winner by goal count for football, or by WIN/LOSS marker for chess
        Participation best = null;
        double bestVal = -1;
        for (Participation p : contest.getParticipations()) {
            String ov = p.getOutcomeValue();
            if (ov == null) continue;
            if ("WIN".equals(ov)) { best = p; break; }
            try {
                double v = Double.parseDouble(ov);
                if (v > bestVal) { bestVal = v; best = p; }
            } catch (NumberFormatException ignored) {}
        }

        List<Participation> ranked = new ArrayList<>(contest.getParticipations());
        Map<Participation, StandingContribution> contributions = new LinkedHashMap<>();

        if (best != null) {
            final Participation winner = best;
            ranked.remove(winner); ranked.add(0, winner);
            boolean draw = ranked.stream().skip(1)
                .allMatch(p -> Objects.equals(p.getOutcomeValue(), winner.getOutcomeValue()));
            for (int i = 0; i < ranked.size(); i++) {
                contributions.put(ranked.get(i),
                    draw ? StandingContribution.drew()
                         : (i == 0 ? StandingContribution.won() : StandingContribution.lost()));
            }
        } else {
            for (Participation p : ranked) contributions.put(p, StandingContribution.drew());
        }

        // Force the contest into the finished state by injecting a pre-built result
        forceFinish(contest, new SimpleResult(ranked, contributions), targetState);
    }

    /** Reflectively bypasses state-machine guards to inject an imported result. */
    private static void forceFinish(Contest contest, SimpleResult result, ContestState state) {
        try {
            var stateField  = Contest.class.getDeclaredField("state");
            var resultField = Contest.class.getDeclaredField("result");
            stateField.setAccessible(true);
            resultField.setAccessible(true);
            stateField.set(contest, state);
            resultField.set(contest, result);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Cannot inject imported result into Contest", e);
        }
    }
}
