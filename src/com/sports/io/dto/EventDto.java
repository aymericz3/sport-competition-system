package com.sports.io.dto;

public class EventDto {
    public String type;         // "goal" | "move"
    // goal fields
    public String scorerRole;
    public int minute;
    // move fields
    public String playerRole;
    public String notation;
    public String termination;  // TerminationKind name, or null for ordinary moves
}
