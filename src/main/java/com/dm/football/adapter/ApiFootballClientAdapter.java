package com.dm.football.adapter;

import com.dm.football.exception.ExternalApiException;
import com.dm.football.response.CountryResponse;
import com.dm.football.response.LeagueResponse;
import com.dm.football.response.StandingResponse;
import com.dm.football.response.TeamResponse;
import com.dm.football.util.JsonConversionUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Adapter (client) for the ApiFootball.com external REST API.
 * Handles all HTTP communication and response mapping
 */
@Component
@Slf4j
public class ApiFootballClientAdapter {

    private final RestTemplate restTemplate;
    @Value("${api.football.url}")
    private String apiUrl;
    @Value("${api.football.key}")
    private String apiKey;

    public ApiFootballClientAdapter(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<CountryResponse> fetchCountries() {
        String url = apiUrl + "/?action=get_countries&APIkey=" + apiKey;
        ResponseEntity<String> response = null;
        try {
            log.info("Request URL for fetchCountries {}", url);
            response = restTemplate.exchange(url, HttpMethod.GET,
                    getBasicHeadersHttpEntity(), new ParameterizedTypeReference<>() {
                    });
            log.info("Fetch Countries response is {}", response);
            TypeReference<List<CountryResponse>> typeReference = new TypeReference<>() {
            };
            return JsonConversionUtil.convertFromJsonSilently(response.getBody(), typeReference);
        } catch (Exception ex) {
            log.error("Error while fetching countries", ex);
            return Collections.emptyList();
        }
    }

    public List<LeagueResponse> fetchLeagues(String countryId) {
        String url = apiUrl + "/?action=get_leagues&country_id=" + countryId + "&APIkey=" + apiKey;
        ResponseEntity<String> response = null;
        try {
            log.info("Request URL for fetchLeagues{}", url);
            response = restTemplate.exchange(url, HttpMethod.GET,
                    getBasicHeadersHttpEntity(), new ParameterizedTypeReference<>() {
                    });
            log.info("Fetch Leagues response is {}", response);
            TypeReference<List<LeagueResponse>> typeReference = new TypeReference<>() {
            };
            return JsonConversionUtil.convertFromJsonSilently(response.getBody(), typeReference);
        } catch (Exception ex) {
            log.error("Error while fetching leagues for country", ex);
            throw new ExternalApiException("Failed to fetch leagues for country: " + countryId, ex);
        }
    }

    public List<TeamResponse> fetchTeams(String leagueId) {
        String url = apiUrl + "/?action=get_teams&league_id=" + leagueId + "&APIkey=" + apiKey;
        ResponseEntity<String> response = null;
        try {
            log.info("Request URL for fetchTeams {}", url);
            response = restTemplate.exchange(url, HttpMethod.GET,
                    getBasicHeadersHttpEntity(), new ParameterizedTypeReference<>() {
                    });
            log.info("Fetch Teams response is {}", response);
            TypeReference<List<TeamResponse>> typeReference = new TypeReference<>() {
            };
            return JsonConversionUtil.convertFromJsonSilently(response.getBody(), typeReference);
        } catch (Exception ex) {
            log.error("Error while fetching teams for league", ex);
            throw new ExternalApiException("Failed to fetch teams for league: " + leagueId, ex);
        }
    }

    public List<StandingResponse> fetchStandings(String leagueId) {
        String url = apiUrl + "/?action=get_standings&league_id=" + leagueId +
                "&APIkey=" + apiKey;
        ResponseEntity<String> response = null;
        try {
            log.info("Request URL for fetchStandings{}", url);
            response = restTemplate.exchange(url, HttpMethod.GET,
                    getBasicHeadersHttpEntity(), new ParameterizedTypeReference<>() {
                    });
            log.info("Fetch Standings response is {}", response);
            TypeReference<List<StandingResponse>> typeReference = new TypeReference<>() {
            };
            return JsonConversionUtil.convertFromJsonSilently(response.getBody(), typeReference);
        } catch (Exception ex) {
            log.error("Error while fetching standings for league", ex);
            throw new ExternalApiException("Failed to fetch standings for league: " + leagueId, ex);
        }
    }

    private HttpEntity getBasicHeadersHttpEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(CONTENT_TYPE, APPLICATION_JSON_VALUE);
        return new HttpEntity<>(headers);
    }
}
