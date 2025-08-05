package com.dm.football.service.impl;

import com.dm.football.adapter.ApiFootballClientAdapter;
import com.dm.football.response.CountryResponse;
import com.dm.football.response.LeagueResponse;
import com.dm.football.response.StandingResponse;
import com.dm.football.response.TeamResponse;
import com.dm.football.service.DataRetrievalStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OnlineDataRetrievalStrategy implements DataRetrievalStrategy {
    private final ApiFootballClientAdapter apiFootballClientAdapter; // Injected client that calls apifootball.com

    @Autowired
    public OnlineDataRetrievalStrategy(ApiFootballClientAdapter apiFootballClientAdapter) {
        this.apiFootballClientAdapter = apiFootballClientAdapter;
    }

    @Override
    public List<CountryResponse> getAllCountries() {
        return apiFootballClientAdapter.fetchCountries();
    }

    @Override
    public List<LeagueResponse> getLeaguesByCountry(String countryId) {
        return apiFootballClientAdapter.fetchLeagues(countryId);
    }

    @Override
    public List<TeamResponse> getTeamsByLeague(String leagueId) {
        return apiFootballClientAdapter.fetchTeams(leagueId);
    }

    @Override
    public List<StandingResponse> getStandings(String leagueId) {
        return apiFootballClientAdapter.fetchStandings(leagueId);
    }
}
