package com.dm.football.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StandingResponse {

    @JsonProperty("country_name")
    private String countryName;

    @JsonProperty("league_id")
    private String leagueId;

    @JsonProperty("league_name")
    private String leagueName;

    @JsonProperty("team_id")
    private String teamId;

    @JsonProperty("team_name")
    private String teamName;

    @JsonProperty("overall_league_position")
    private String overallLeaguePosition;

    @JsonProperty("overall_league_payed")
    private String overallLeaguePlayed;

    @JsonProperty("overall_league_W")
    private String overallLeagueWins;

    @JsonProperty("overall_league_D")
    private String overallLeagueDraws;

    @JsonProperty("overall_league_L")
    private String overallLeagueLosses;

    @JsonProperty("overall_league_GF")
    private String overallLeagueGoalsFor;

    @JsonProperty("overall_league_GA")
    private String overallLeagueGoalsAgainst;

    @JsonProperty("overall_league_PTS")
    private String overallLeaguePoints;

    @JsonProperty("team_badge")
    private String teamBadge;
}
