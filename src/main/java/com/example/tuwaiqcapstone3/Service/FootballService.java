package com.example.tuwaiqcapstone3.Service;

import com.example.tuwaiqcapstone3.API.ApiException;
import com.example.tuwaiqcapstone3.DTO.FootballResponseDTO;
import com.example.tuwaiqcapstone3.Model.Match;
import com.example.tuwaiqcapstone3.Model.Stadium;
import com.example.tuwaiqcapstone3.Repository.MatchRepository;
import com.example.tuwaiqcapstone3.Repository.StadiumRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class FootballService {

    private final MatchRepository matchRepository;
    private final StadiumRepository stadiumRepository;

    @Value("${football.api.url}")
    private String apiUrl;

    @Value("${football.api.key}")
    private String apiKey;

    private static final ZoneId SAUDI_ZONE = ZoneId.of("Asia/Riyadh");

    private static final LocalDate ORIGINAL_FIRST_DAY = LocalDate.of(2024, 1, 12);

    private static final LocalDate TEST_FIRST_DAY = LocalDate.of(2027, 1, 7);

    private FootballResponseDTO fetchMatches() {
        FootballResponseDTO response;

        try {
            response = RestClient.create(apiUrl).get().uri("/fixtures?league=7&season=2023").header("x-apisports-key", apiKey).retrieve().body(FootballResponseDTO.class);
        } catch (RestClientException e) {
            throw new ApiException("failed to fetch matches from football API");
        }

        if (response == null) {
            throw new ApiException("football API returned an empty response");
        }

        if (response.getErrors() != null && !response.getErrors().isNull() && !response.getErrors().isEmpty()) {
            throw new ApiException("football API returned errors: " + response.getErrors());
        }

        if (response.getResponse() == null || response.getResponse().isEmpty()) {
            throw new ApiException("no matches found in football API");
        }

        if (response.getPaging() == null || response.getPaging().getTotal() == null) {
            throw new ApiException("football API pagination information is missing");
        }

        if (response.getPaging().getTotal() > 1) {
            throw new ApiException("football API returned multiple pages; import requires all pages");
        }

        return response;
    }

    @Transactional
    public Integer importTestMatches() {
        FootballResponseDTO response = fetchMatches();

        long daysDifference = ChronoUnit.DAYS.between(ORIGINAL_FIRST_DAY, TEST_FIRST_DAY);
        int importedCount = 0;

        for (FootballResponseDTO.MatchDTO matchDTO : response.getResponse()) {
            if (matchDTO == null || matchDTO.getFixture() == null || matchDTO.getLeague() == null || matchDTO.getTeams() == null) {
                throw new ApiException("match data is incomplete");
            }

            FootballResponseDTO.FixtureDTO fixture = matchDTO.getFixture();
            FootballResponseDTO.LeagueDTO league = matchDTO.getLeague();
            FootballResponseDTO.TeamsDTO teams = matchDTO.getTeams();

            if (fixture.getId() == null || fixture.getDate() == null) {
                throw new ApiException("match id or date is missing");
            }

            if (!Integer.valueOf(7).equals(league.getId()) || !Integer.valueOf(2023).equals(league.getSeason())) {
                throw new ApiException("unexpected league or season");
            }

            if (league.getRound() == null || league.getRound().isBlank()) {
                throw new ApiException("match round is missing");
            }

            if (fixture.getStatus() == null || fixture.getStatus().getShortStatus() == null) {
                throw new ApiException("match status is missing");
            }

            if (teams.getHome() == null || teams.getAway() == null) {
                throw new ApiException("match teams are missing");
            }

            if (teams.getHome().getName() == null || teams.getHome().getName().isBlank() || teams.getAway().getName() == null || teams.getAway().getName().isBlank()) {
                throw new ApiException("team names are missing");
            }

            FootballResponseDTO.VenueDTO venue = fixture.getVenue();

            if (venue == null || venue.getName() == null || venue.getName().isBlank() || venue.getCity() == null || venue.getCity().isBlank()) {
                throw new ApiException("stadium information is missing");
            }

            Stadium stadium = stadiumRepository.findStadiumByNameIgnoreCaseAndCityIgnoreCase(venue.getName().trim(), venue.getCity().trim());

            if (stadium == null) {
                throw new ApiException("add stadium first: " + venue.getName() + " - " + venue.getCity());
            }

            LocalDateTime originalStart = fixture.getDate().atZoneSameInstant(SAUDI_ZONE).toLocalDateTime();
            LocalDateTime testStart = originalStart.plusDays(daysDifference);

            int estimatedMinutes;

            if (league.getRound().startsWith("Group Stage")) {
                estimatedMinutes = 120;
            } else {
                estimatedMinutes = 180;
            }

            Match match = matchRepository.findMatchByExternalFixtureId(fixture.getId());

            if (match == null) {
                match = new Match();
                match.setExternalFixtureId(fixture.getId());
            }

            match.setStadium(stadium);
            match.setHomeTeam(teams.getHome().getName());
            match.setAwayTeam(teams.getAway().getName());
            match.setHomeTeamExternalId(teams.getHome().getId());
            match.setAwayTeamExternalId(teams.getAway().getId());
            match.setSeason(league.getSeason());
            match.setRound(league.getRound());
            match.setExternalStatus(fixture.getStatus().getShortStatus());
            match.setStartTime(testStart);
            match.setExpectedEndTime(testStart.plusMinutes(estimatedMinutes));
            match.setStatus("scheduled");

            matchRepository.save(match);
            importedCount++;
        }

        return importedCount;
    }
}