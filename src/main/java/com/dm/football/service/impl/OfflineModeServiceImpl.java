package com.dm.football.service.impl;

import com.dm.football.response.CountryResponse;
import com.dm.football.response.LeagueResponse;
import com.dm.football.response.StandingResponse;
import com.dm.football.response.TeamResponse;
import com.dm.football.service.OfflineModeService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OfflineModeServiceImpl implements OfflineModeService {

    private final Map<String, List<CountryResponse>> offlineCountriesCache = new ConcurrentHashMap<>();
    private final Map<String, List<LeagueResponse>> offlineLeaguesCache = new ConcurrentHashMap<>();
    private final Map<String, List<TeamResponse>> offlineTeamsCache = new ConcurrentHashMap<>();

    private final Map<String, List<StandingResponse>> offlineStandingsCache = new ConcurrentHashMap<>();
    //private final Map<String, StandingResponse> offlineCache = new ConcurrentHashMap<>();

    private boolean offlineMode = false;

    @Override
    public void setOfflineMode(boolean enabled) {
        this.offlineMode = enabled;
    }

    @Override
    public boolean isOfflineMode() {
        return offlineMode;
    }

    // When offline, fetch from in-memory cache instead of external API
/*    @Override
    public Optional<StandingResponse> getStanding(String country, String league, String team) {
        String key = String.join("-", country, league, team).toLowerCase();
        return Optional.ofNullable(offlineCache.get(key));
    }*/

    @Override
    public List<CountryResponse> getAllCountries() {
        String key = "countries";
        return offlineCountriesCache.get(key);
    }

    @Override
    public List<LeagueResponse> getLeaguesByCountry(String countryId) {
        String key = String.join("-", "leagues", countryId).toLowerCase();
        return offlineLeaguesCache.get(key);
    }

    @Override
    public List<TeamResponse> getTeamsByLeague(String leagueId) {
        String key = String.join("-", "teams", leagueId).toLowerCase();
        return offlineTeamsCache.get(key);
    }

    @Override
    public List<StandingResponse> getStandings(String leagueId) {
        String key = String.join("-", "standings", leagueId).toLowerCase();
        return offlineStandingsCache.get(key);
    }

    // Populate offline cache periodically or during online usage
    /*public void cacheStanding(String country, String league, String team, StandingResponse response) {
        String key = String.join("-", country, league, team).toLowerCase();
        offlineCache.put(key, response);
    }*/

    public void cacheStandings(String leagueId, List<StandingResponse> response) {
        String key = String.join("-", "standings", leagueId).toLowerCase();
        offlineStandingsCache.put(key, response);
    }


    // Populate offline cache periodically or during online usage
    public void cacheCountries(List<CountryResponse> response) {
        String key = "countries";
        offlineCountriesCache.put(key, response);
    }

    // Populate offline cache periodically or during online usage
    public void cacheLeagues(String countryId, List<LeagueResponse> response) {
        String key = String.join("-", "leagues", countryId).toLowerCase();
        offlineLeaguesCache.put(key, response);
    }

    // Populate offline cache periodically or during online usage
    public void cacheTeams(String leagueId, List<TeamResponse> response) {
        String key = String.join("-", "teams", leagueId).toLowerCase();
        offlineTeamsCache.put(key, response);
    }
}
