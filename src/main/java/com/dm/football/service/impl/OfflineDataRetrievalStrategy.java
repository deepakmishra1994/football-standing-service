package com.dm.football.service.impl;

import com.dm.football.response.CountryResponse;
import com.dm.football.response.LeagueResponse;
import com.dm.football.response.StandingResponse;
import com.dm.football.response.TeamResponse;
import com.dm.football.service.DataRetrievalStrategy;
import com.dm.football.service.OfflineModeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OfflineDataRetrievalStrategy implements DataRetrievalStrategy {
    private final OfflineModeService offlineModeService; // Handles local/in-memory data

    @Autowired
    public OfflineDataRetrievalStrategy(OfflineModeService offlineModeService) {
        this.offlineModeService = offlineModeService;
    }
    @Override
    public List<CountryResponse> getAllCountries() {
        return offlineModeService.getAllCountries();
    }

    @Override
    public List<LeagueResponse> getLeaguesByCountry(String countryId) {
        return offlineModeService.getLeaguesByCountry(countryId);
    }

    @Override
    public List<TeamResponse> getTeamsByLeague(String leagueId) {
        return offlineModeService.getTeamsByLeague(leagueId);
    }

    @Override
    public List<StandingResponse> getStandings(String leagueId) {
        return offlineModeService.getStandings(leagueId);
    }
}

