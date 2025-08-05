package com.dm.football.service;

import com.dm.football.response.CountryResponse;
import com.dm.football.response.LeagueResponse;
import com.dm.football.response.StandingResponse;
import com.dm.football.response.TeamResponse;

import java.util.List;

public interface DataRetrievalStrategy {

    List<CountryResponse> getAllCountries();

    List<LeagueResponse> getLeaguesByCountry(String countryId);

    List<TeamResponse> getTeamsByLeague(String leagueId);

    List<StandingResponse> getStandings(String leagueId);
}
