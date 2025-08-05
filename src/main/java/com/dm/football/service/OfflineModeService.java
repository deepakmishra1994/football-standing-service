package com.dm.football.service;

import com.dm.football.response.CountryResponse;
import com.dm.football.response.LeagueResponse;
import com.dm.football.response.StandingResponse;
import com.dm.football.response.TeamResponse;

import java.util.List;

public interface OfflineModeService {
    boolean isOfflineMode();

    // Toggle offline/online mode
    void setOfflineMode(boolean enabled);
    // Service logic for fetching standings and other data in offline mode

    void cacheCountries(List<CountryResponse> countries);

    void cacheLeagues(String countryId, List<LeagueResponse> response);

    void cacheTeams(String leagueId, List<TeamResponse> response);

    void cacheStandings(String leagueId, List<StandingResponse> response);

    List<CountryResponse> getAllCountries();

    List<LeagueResponse> getLeaguesByCountry(String countryId);

    List<TeamResponse> getTeamsByLeague(String leagueId);

    List<StandingResponse> getStandings(String leagueId);
}
