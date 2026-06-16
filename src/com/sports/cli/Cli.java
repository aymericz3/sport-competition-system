package com.sports.cli;

import com.sports.app.InMemoryRepository;
import com.sports.core.competition.Competition;
import com.sports.core.competition.Leaderboard;
import com.sports.core.contest.*;
import com.sports.core.entity.Participant;
import com.sports.io.JsonStore;
import com.sports.modules.chess.*;
import com.sports.modules.football.*;
import java.io.IOException;
import java.util.*;

/**
 * Console application: a thin command loop over the domain.
 * CRUD lives here so that the domain model stays rule-driven.
 */
public class Cli {

    private final InMemoryRepository<Participant>  participants  = new InMemoryRepository<>();
    private final InMemoryRepository<Competition>  competitions  = new InMemoryRepository<>();
    private final Scanner sc;

    public Cli(Scanner sc) { this.sc = sc; }

    public void run() {
        System.out.println("Sports Competition Framework — type 'help' for commands.");
        while (sc.hasNextLine()) {
            System.out.print("> ");
            String line = sc.nextLine().trim();
            if (line.isEmpty()) continue;
            try {
                handle(line);
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    // ── Command dispatcher ────────────────────────────────────────────────────

    private void handle(String line) throws IOException {
        String[] parts = line.split("\\s+", 3);
        String cmd = parts[0].toLowerCase();
        switch (cmd) {
            case "help"            -> help();
            case "add"             -> addParticipant(parts);
            case "participants"    -> listParticipants();
            case "new"             -> newCompetition(parts);
            case "list"            -> listCompetitions();
            case "contests"        -> listContests(parts);
            case "start"           -> startContest(parts);
            case "goal"            -> recordGoal(parts);
            case "time"            -> setTime(parts);
            case "move"            -> recordMove(parts);
            case "checkmate"       -> declareCheckmate(parts);
            case "resign"          -> declareResignation(parts);
            case "draw"            -> declareDraw(parts);
            case "leaderboard"     -> leaderboard(parts);
            case "next-round"      -> nextRound(parts);
            case "import"          -> importComp(parts);
            case "export"          -> exportComp(parts);
            case "quit", "exit"    -> { System.out.println("Bye."); System.exit(0); }
            default                -> System.out.println("Unknown command. Type 'help'.");
        }
    }

    // ── Participant CRUD ──────────────────────────────────────────────────────

    private void addParticipant(String[] parts) {
        // add <name> [team]
        if (parts.length < 2) { System.out.println("Usage: add <name> [team]"); return; }
        String name   = parts[1];
        boolean isTeam = parts.length >= 3 && "team".equalsIgnoreCase(parts[2]);
        Participant p  = isTeam ? Participant.team(name) : Participant.individual(name);
        participants.save(name, p);
        System.out.println("Added " + (isTeam ? "team" : "player") + ": " + name);
    }

    private void listParticipants() {
        if (participants.size() == 0) { System.out.println("No participants yet."); return; }
        participants.findAll().forEach(p ->
            System.out.println("  " + p.getName() + (p.isTeam() ? " [team]" : "")));
    }

    // ── Competition CRUD ──────────────────────────────────────────────────────

    private void newCompetition(String[] parts) {
        // new football <name> | new chess <name>
        if (parts.length < 3) { System.out.println("Usage: new football|chess <name>"); return; }
        String type = parts[1].toLowerCase();
        String name = parts[2];
        if (participants.size() == 0) { System.out.println("Add participants first."); return; }
        List<Participant> ps = new ArrayList<>(participants.findAll());
        Competition comp = switch (type) {
            case "football" -> FootballModule.league(name, ps);
            case "chess"    -> ChessModule.swiss(name, ps);
            default         -> throw new IllegalArgumentException("Unknown type: " + type);
        };
        competitions.save(name, comp);
        System.out.println("Created " + type + " competition '" + name + "' with "
            + ps.size() + " participants, " + comp.getContests().size() + " fixtures generated.");
    }

    private void listCompetitions() {
        if (competitions.size() == 0) { System.out.println("No competitions yet."); return; }
        competitions.findAll().forEach(c ->
            System.out.println("  " + c.getName() + " [" + c.getSport().getName() + "] "
                + c.getContests().size() + " contests"));
    }

    // ── Contest operations ────────────────────────────────────────────────────

    private void listContests(String[] parts) {
        // contests <comp-name>
        Competition comp = requireComp(parts, 1);
        if (comp == null) return;
        List<Contest> list = comp.getContests();
        for (int i = 0; i < list.size(); i++) {
            Contest c = list.get(i);
            System.out.printf("  [%d] %s  state=%s%n", i + 1,
                participantNames(c), c.getState());
        }
    }

    private void startContest(String[] parts) {
        // start <comp> <index>
        Contest c = requireContest(parts);
        if (c == null) return;
        c.start();
        System.out.println("Started: " + participantNames(c));
    }

    private void recordGoal(String[] parts) {
        // goal <comp> <index> <role> <minute>
        if (parts.length < 3) { System.out.println("Usage: goal <comp> <idx> <role> <min>"); return; }
        String[] sub = parts[2].split("\\s+");
        if (sub.length < 2) { System.out.println("Usage: goal <comp> <idx> <role> <min>"); return; }
        Contest c = requireContest(parts);
        if (c == null) return;
        String role = sub[0].toUpperCase();
        int minute  = Integer.parseInt(sub[1]);
        Participation scorer = findByRole(c, role);
        if (scorer == null) { System.out.println("No participation with role " + role); return; }
        c.recordEvent(new GoalEvent(scorer, minute));
        System.out.println("Goal recorded for " + scorer.getParticipant().getName()
            + " at minute " + minute + ". State: " + c.getState());
    }

    private void setTime(String[] parts) {
        // time <comp> <index> <minutes>
        if (parts.length < 3) { System.out.println("Usage: time <comp> <idx> <minutes>"); return; }
        String[] sub = parts[2].split("\\s+");
        Contest c = requireContest(parts);
        if (c == null) return;
        int mins = Integer.parseInt(sub[0]);
        c.setElapsedMinutes(mins);
        System.out.println("Elapsed set to " + mins + " min. State: " + c.getState()
            + (c.isDecided() ? " → " + contestSummary(c) : ""));
    }

    private void recordMove(String[] parts) {
        // move <comp> <index> <role> <notation>
        if (parts.length < 3) { System.out.println("Usage: move <comp> <idx> <role> <notation>"); return; }
        String[] sub = parts[2].split("\\s+", 2);
        Contest c = requireContest(parts);
        if (c == null) return;
        String role = sub[0].toUpperCase();
        String notation = sub.length > 1 ? sub[1] : "?";
        Participation player = findByRole(c, role);
        if (player == null) { System.out.println("No participation with role " + role); return; }
        c.recordEvent(new MoveEvent(player, notation));
        System.out.println("Move recorded: " + notation + " by " + player);
    }

    private void declareCheckmate(String[] parts) {
        // checkmate <comp> <index> <role>   (role = the player who delivered mate)
        if (parts.length < 3) { System.out.println("Usage: checkmate <comp> <idx> <role>"); return; }
        Contest c = requireContest(parts);
        if (c == null) return;
        String role = parts[2].trim().toUpperCase();
        Participation p = findByRole(c, role);
        if (p == null) { System.out.println("No participation with role " + role); return; }
        c.recordEvent(new MoveEvent(p, "#", TerminationKind.CHECKMATE));
        System.out.println("Checkmate by " + p.getParticipant().getName() + ". " + contestSummary(c));
    }

    private void declareResignation(String[] parts) {
        // resign <comp> <index> <role>   (role = the player who resigns)
        if (parts.length < 3) { System.out.println("Usage: resign <comp> <idx> <role>"); return; }
        Contest c = requireContest(parts);
        if (c == null) return;
        String role = parts[2].trim().toUpperCase();
        Participation p = findByRole(c, role);
        if (p == null) { System.out.println("No participation with role " + role); return; }
        c.recordEvent(new MoveEvent(p, "resigns", TerminationKind.RESIGNATION));
        System.out.println(p.getParticipant().getName() + " resigned. " + contestSummary(c));
    }

    private void declareDraw(String[] parts) {
        // draw <comp> <index> <kind>  kind = agreed|repetition|stalemate
        if (parts.length < 3) { System.out.println("Usage: draw <comp> <idx> agreed|repetition|stalemate"); return; }
        Contest c = requireContest(parts);
        if (c == null) return;
        TerminationKind kind = switch (parts[2].trim().toLowerCase()) {
            case "agreed"      -> TerminationKind.DRAW_AGREED;
            case "repetition"  -> TerminationKind.THREEFOLD_REPETITION;
            case "stalemate"   -> TerminationKind.STALEMATE;
            default            -> throw new IllegalArgumentException("Unknown draw kind: " + parts[2]);
        };
        // Use the first active player as event actor (doesn't matter for draws)
        Participation actor = c.getParticipations().get(0);
        c.recordEvent(new MoveEvent(actor, "draw", kind));
        System.out.println("Draw declared (" + kind + "). " + contestSummary(c));
    }

    private void leaderboard(String[] parts) {
        Competition comp = requireComp(parts, 1);
        if (comp == null) return;
        Leaderboard lb = comp.computeStandings();
        if (lb.getEntries().isEmpty()) { System.out.println("No decided contests yet."); return; }
        System.out.println("=== " + comp.getName() + " Standings ===");
        lb.getEntries().forEach(e -> System.out.println("  " + e));
    }

    private void nextRound(String[] parts) {
        Competition comp = requireComp(parts, 1);
        if (comp == null) return;
        comp.generateNextRound();
        System.out.println("Next round generated. Total contests: " + comp.getContests().size());
    }

    private void importComp(String[] parts) throws IOException {
        if (parts.length < 2) { System.out.println("Usage: import <file>"); return; }
        Competition comp = JsonStore.load(parts[1]);
        competitions.save(comp.getName(), comp);
        System.out.println("Imported: " + comp.getName() + " (" + comp.getContests().size() + " contests)");
    }

    private void exportComp(String[] parts) throws IOException {
        if (parts.length < 3) { System.out.println("Usage: export <comp-name> <file>"); return; }
        Competition comp = requireComp(parts, 1);
        if (comp == null) return;
        JsonStore.save(comp, parts[2]);
        System.out.println("Exported to " + parts[2]);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Competition requireComp(String[] parts, int idx) {
        if (parts.length <= idx) { System.out.println("Missing competition name."); return null; }
        String name = parts[idx];
        Optional<Competition> opt = competitions.find(name);
        if (opt.isEmpty()) { System.out.println("Competition not found: " + name); return null; }
        return opt.get();
    }

    private Contest requireContest(String[] parts) {
        // parts[1] = comp name, parts[2] starts with "<index> ..."
        if (parts.length < 3) { System.out.println("Missing index."); return null; }
        Competition comp = requireComp(parts, 1);
        if (comp == null) return null;
        String[] sub = parts[2].split("\\s+");
        int idx = Integer.parseInt(sub[0]) - 1;
        List<Contest> list = comp.getContests();
        if (idx < 0 || idx >= list.size()) {
            System.out.println("Index out of range (1–" + list.size() + ")");
            return null;
        }
        // Shift remainder of sub into parts[2] for callers that parse further
        parts[2] = sub.length > 1
            ? String.join(" ", Arrays.copyOfRange(sub, 1, sub.length))
            : "";
        return list.get(idx);
    }

    private Participation findByRole(Contest c, String role) {
        return c.getParticipations().stream()
            .filter(p -> role.equalsIgnoreCase(p.getRole()))
            .findFirst().orElse(null);
    }

    private String participantNames(Contest c) {
        StringBuilder sb = new StringBuilder();
        for (Participation p : c.getParticipations()) {
            if (!sb.isEmpty()) sb.append(" vs ");
            sb.append(p.getParticipant().getName()).append("[").append(p.getRole()).append("]");
        }
        return sb.toString();
    }

    private String contestSummary(Contest c) {
        if (!c.isDecided() || c.getResult() == null) return "not decided";
        StringBuilder sb = new StringBuilder("Result: ");
        for (Participation p : c.getResult().getRankedParticipations()) {
            StandingContribution sc = c.getResult().getContribution(p);
            sb.append(p.getParticipant().getName())
              .append("(").append(sc != null ? sc.getToken() : "?").append(") ");
        }
        return sb.toString().trim();
    }

    private void help() {
        System.out.println("""
            add <name> [team]              Add a participant
            participants                   List participants
            new football|chess <name>      Create competition with all current participants
            list                           List competitions
            contests <comp>                List contests (with index)
            start <comp> <idx>             Start contest
            goal <comp> <idx> <role> <min> Record a goal (football)
            time <comp> <idx> <minutes>    Set elapsed time / trigger match end
            move <comp> <idx> <role> <notation>  Record a chess move
            checkmate <comp> <idx> <role>  Declare checkmate (role = player who mates)
            resign <comp> <idx> <role>     Declare resignation (role = player who resigns)
            draw <comp> <idx> agreed|repetition|stalemate
            leaderboard <comp>             Show standings
            next-round <comp>              Generate next Swiss round
            import <file>                  Import competition from JSON
            export <comp> <file>           Export competition to JSON
            quit                           Exit""");
    }
}
