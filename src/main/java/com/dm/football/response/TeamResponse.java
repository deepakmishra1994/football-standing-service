package com.dm.football.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamResponse {

    @JsonProperty("team_key")
    private String teamKey;

    @JsonProperty("team_name")
    private String teamName;

    @JsonProperty("team_country")
    private String teamCountry;

    @JsonProperty("team_founded")
    private String teamFounded;

    @JsonProperty("team_badge")
    private String teamBadge;
}