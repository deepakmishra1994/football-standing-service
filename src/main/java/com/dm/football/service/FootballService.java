package com.dm.football.service;

import com.dm.football.exception.TeamNotFoundException;
import com.dm.football.factory.DataRetrievalStrategyFactory;
import com.dm.football.response.CountryResponse;
import com.dm.football.response.LeagueResponse;
import com.dm.football.response.StandingResponse;
import com.dm.football.response.TeamResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FootballService {
    private final DataRetrievalStrategyFactory strategyFactory;
    private final OfflineModeService offlineModeService;

    public List<CountryResponse> getAllCountries() {
        DataRetrievalStrategy strategy = strategyFactory.getStrategy(offlineModeService.isOfflineMode());
        var allCountries = strategy.getAllCountries();
        if (!offlineModeService.isOfflineMode()) {
            offlineModeService.cacheCountries(allCountries);
        }
        return allCountries;
    }

    public List<LeagueResponse> getLeaguesByCountry(String countryId) {
        DataRetrievalStrategy strategy = strategyFactory.getStrategy(offlineModeService.isOfflineMode());
        var allLeagues = strategy.getLeaguesByCountry(countryId);
        if (!offlineModeService.isOfflineMode()) {
            offlineModeService.cacheLeagues(countryId, allLeagues);
        }
        return allLeagues;
    }

    public List<TeamResponse> getTeamsByLeague(String leagueId) {
        DataRetrievalStrategy strategy = strategyFactory.getStrategy(offlineModeService.isOfflineMode());
        var allTeams = strategy.getTeamsByLeague(leagueId);
        if (!offlineModeService.isOfflineMode()) {
            offlineModeService.cacheTeams(leagueId, allTeams);
        }
        return allTeams;
    }

    public List<StandingResponse> getStandings(String leagueId) {
        DataRetrievalStrategy strategy = strategyFactory.getStrategy(offlineModeService.isOfflineMode());
        var standings = strategy.getStandings(leagueId);
        if (!offlineModeService.isOfflineMode()) {
            offlineModeService.cacheStandings(leagueId, standings);
        }
        return standings;
    }

    public StandingResponse getTeamStanding(String country, String leagueId, String team) {
        var standings = getStandings(leagueId);
        return standings.stream()
                .filter(standing -> standing.getTeamName().equalsIgnoreCase(team)
                        && standing.getCountryName().equalsIgnoreCase(country))
                .findFirst()
                .orElseThrow(() -> new TeamNotFoundException(
                        String.format("Team '%s' not found in leagueId '%s' for country '%s'", team, leagueId, country)));
    }
}
