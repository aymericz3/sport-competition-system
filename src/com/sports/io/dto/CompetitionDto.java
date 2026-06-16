package com.sports.io.dto;

import java.util.List;

public class CompetitionDto {
    public String id;
    public String name;
    public String type;            // "football_league" | "chess_swiss"
    public List<ParticipantDto> participants;
    public List<ContestDto> contests;
}
