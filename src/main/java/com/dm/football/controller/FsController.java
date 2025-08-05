package com.dm.football.controller;

import com.dm.football.response.CountryResponse;
import com.dm.football.response.LeagueResponse;
import com.dm.football.response.StandingResponse;
import com.dm.football.response.TeamResponse;
import com.dm.football.service.FootballService;
import com.dm.football.service.OfflineModeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@Slf4j
@RequiredArgsConstructor
public class FsController {

    private final FootballService footballService;
    private final OfflineModeService offlineModeService;

    @Operation(summary = "Get all available countries")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved countries"),
            @ApiResponse(responseCode = "503", description = "Service unavailable")
    })
    @GetMapping("/countries")
    public ResponseEntity<CollectionModel<EntityModel<CountryResponse>>> getCountries() {
        log.info("Request received for getting all countries");

        List<CountryResponse> countries = footballService.getAllCountries();

        List<EntityModel<CountryResponse>> countryModels = countries.stream()
                .map(country -> EntityModel.of(country)
                        .add(linkTo(methodOn(FsController.class).getLeagues(country.getCountryId())).withRel("leagues"))
                        .add(linkTo(methodOn(FsController.class).getCountries()).withSelfRel()))
                .toList();

        CollectionModel<EntityModel<CountryResponse>> collectionModel = CollectionModel.of(countryModels)
                .add(linkTo(methodOn(FsController.class).getCountries()).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }


    @Operation(summary = "Get teams by league ID")
    @GetMapping("/teams/{leagueId}")
    public ResponseEntity<CollectionModel<EntityModel<TeamResponse>>> getTeams(
            @Parameter(description = "League ID") @PathVariable String leagueId) {

        log.info("Request received for getting teams for league: {}", leagueId);

        List<TeamResponse> teams = footballService.getTeamsByLeague(leagueId);

        List<EntityModel<TeamResponse>> teamModels = teams.stream()
                .map(team -> EntityModel.of(team)
                        .add(linkTo(methodOn(FsController.class).getTeams(leagueId)).withSelfRel()))
                .toList();

        CollectionModel<EntityModel<TeamResponse>> collectionModel = CollectionModel.of(teamModels)
                .add(linkTo(methodOn(FsController.class).getTeams(leagueId)).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    @Operation(summary = "Get leagues by country ID")
    @GetMapping("/leagues/{countryId}")
    public ResponseEntity<CollectionModel<EntityModel<LeagueResponse>>> getLeagues(
            @Parameter(description = "Country ID") @PathVariable String countryId) {

        log.info("Request received for getting leagues for country: {}", countryId);

        List<LeagueResponse> leagues = footballService.getLeaguesByCountry(countryId);

        List<EntityModel<LeagueResponse>> leagueModels = leagues.stream()
                .map(league -> EntityModel.of(league)
                        .add(linkTo(methodOn(FsController.class).getTeams(league.getLeagueId())).withRel("teams"))
                        .add(linkTo(methodOn(FsController.class).getStandings(league.getLeagueId())).withRel("standings"))
                        .add(linkTo(methodOn(FsController.class).getLeagues(countryId)).withSelfRel()))
                .toList();

        CollectionModel<EntityModel<LeagueResponse>> collectionModel = CollectionModel.of(leagueModels)
                .add(linkTo(methodOn(FsController.class).getLeagues(countryId)).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    @Operation(summary = "Get standings for a league")
    @GetMapping("/standings/{leagueId}")
    public ResponseEntity<CollectionModel<EntityModel<StandingResponse>>> getStandings(
            @Parameter(description = "League ID") @PathVariable String leagueId) {

        log.info("Request received for getting standings for league: {}", leagueId);

        List<StandingResponse> standings = footballService.getStandings(leagueId);

        List<EntityModel<StandingResponse>> standingModels = standings.stream()
                .map(standing -> EntityModel.of(standing)
                        .add(linkTo(methodOn(FsController.class).getTeamStanding(
                                standing.getCountryName(), standing.getLeagueId(), standing.getTeamName())).withRel("team-details"))
                        .add(linkTo(methodOn(FsController.class).getStandings(leagueId)).withSelfRel()))
                .toList();

        CollectionModel<EntityModel<StandingResponse>> collectionModel = CollectionModel.of(standingModels)
                .add(linkTo(methodOn(FsController.class).getStandings(leagueId)).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    @Operation(summary = "Get specific team standing")
    @GetMapping("/team-standing/{country}/{leagueId}/{team}")
    public ResponseEntity<EntityModel<StandingResponse>> getTeamStanding(
            @Parameter(description = "Country name")
            @PathVariable
            String country,

            @Parameter(description = "League Id")
            @PathVariable
            String leagueId,

            @Parameter(description = "Team name")
            @PathVariable
            String team) {

        log.info("Request received for team standing: {}/{}/{}", country, leagueId, team);

        StandingResponse standing = footballService.getTeamStanding(country, leagueId, team);

        EntityModel<StandingResponse> standingModel = EntityModel.of(standing)
                .add(linkTo(methodOn(FsController.class).getTeamStanding(country, leagueId, team)).withSelfRel())
                .add(linkTo(methodOn(FsController.class).getStandings(standing.getLeagueId())).withRel("league-standings"));

        return ResponseEntity.ok(standingModel);
    }

    @Operation(summary = "Toggle offline mode")
    @PostMapping("/offline-mode/{enabled}")
    public ResponseEntity<String> toggleOfflineMode(
            @Parameter(description = "Enable/disable offline mode") @PathVariable boolean enabled) {

        log.info("Request received to toggle offline mode: {}", enabled);

        offlineModeService.setOfflineMode(enabled);
        String message = enabled ? "Offline mode enabled" : "Online mode enabled";

        return ResponseEntity.ok(message);
    }
}

