package com.sports.io.dto;

import java.util.List;

public class ContestDto {
    public String id;
    public String state;               // ContestState name
    public List<ParticipationDto> participations;
    public List<EventDto> events;
}
