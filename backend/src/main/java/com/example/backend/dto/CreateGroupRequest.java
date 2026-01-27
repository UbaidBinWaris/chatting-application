package com.example.backend.dto;

import java.util.List;

public class CreateGroupRequest {
    private String name;
    private List<Long> participantIds;

    public CreateGroupRequest() {}

    public CreateGroupRequest(String name, List<Long> participantIds) {
        this.name = name;
        this.participantIds = participantIds;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<Long> getParticipantIds() { return participantIds; }
    public void setParticipantIds(List<Long> participantIds) { this.participantIds = participantIds; }
}
