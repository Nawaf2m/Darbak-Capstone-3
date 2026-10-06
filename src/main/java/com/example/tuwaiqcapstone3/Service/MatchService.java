package com.example.tuwaiqcapstone3.Service;

import com.example.tuwaiqcapstone3.API.ApiException;
import com.example.tuwaiqcapstone3.Model.Match;
import com.example.tuwaiqcapstone3.Model.Stadium;
import com.example.tuwaiqcapstone3.Model.User;
import com.example.tuwaiqcapstone3.Repository.MatchRepository;
import com.example.tuwaiqcapstone3.Repository.StadiumRepository;
import com.example.tuwaiqcapstone3.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MatchService {

    private final MatchRepository matchRepository;
    private final StadiumRepository stadiumRepository;
    private final UserRepository userRepository;

    public List<Match> getMatches() {
        return matchRepository.findAll();
    }

    public Match getMatchById(Integer id) {
        Match match = matchRepository.findMatchById(id);

        if (match == null) {
            throw new ApiException("match not found");
        }

        return match;
    }

    // Returns matches scheduled at the given stadium.
    public List<Match> getMatchesByStadiumId(Integer stadiumId) {
        Stadium stadium = stadiumRepository.findStadiumById(stadiumId);

        if (stadium == null) {
            throw new ApiException("stadium not found");
        }

        List<Match> matches = matchRepository.findMatchesByStadium_Id(stadiumId);

        if (matches.isEmpty()) {
            throw new ApiException("no matches found for this stadium");
        }

        return matches;
    }

    // Returns matches where the team plays at home or away.
    public List<Match> getMatchesByTeam(String teamName) {
        if (teamName == null || teamName.isBlank()) {
            throw new ApiException("team name is required");
        }

        String team = teamName.trim();
        List<Match> matches = matchRepository.findMatchesByHomeTeamIgnoreCaseOrAwayTeamIgnoreCase(team, team);

        if (matches.isEmpty()) {
            throw new ApiException("no matches found for this team");
        }

        return matches;
    }

    // Returns matches held at stadiums in the given city.
    public List<Match> getMatchesByCity(String city) {
        if (city == null || city.isBlank()) {
            throw new ApiException("city is required");
        }

        List<Match> matches = matchRepository.findMatchesByStadium_CityIgnoreCase(city.trim());

        if (matches.isEmpty()) {
            throw new ApiException("no matches found for this city");
        }

        return matches;
    }

    public void addMatch(Match match) {
        Stadium stadium = getExistingStadium(match);
        validateMatch(match);

        match.setStadium(stadium);
        matchRepository.save(match);
    }

    public void updateMatch(Integer id, Match match) {
        Match oldMatch = matchRepository.findMatchById(id);

        if (oldMatch == null) {
            throw new ApiException("match not found");
        }

        Stadium stadium = getExistingStadium(match);
        validateMatch(match);

        oldMatch.setStadium(stadium);
        oldMatch.setHomeTeam(match.getHomeTeam());
        oldMatch.setAwayTeam(match.getAwayTeam());
        oldMatch.setStartTime(match.getStartTime());
        oldMatch.setExpectedEndTime(match.getExpectedEndTime());
        oldMatch.setStatus(match.getStatus());

        matchRepository.save(oldMatch);
    }

    public void deleteMatch(Integer id) {
        Match match = matchRepository.findMatchById(id);

        if (match == null) {
            throw new ApiException("match not found");
        }

        List<User> users = userRepository.findDistinctByMatches_Id(id);
        for (User user : users) {
            user.getMatches().removeIf(savedMatch -> savedMatch.getId().equals(id));
        }
        userRepository.saveAll(users);
        matchRepository.delete(match);
    }

    private Stadium getExistingStadium(Match match) {
        if (match.getStadium() == null || match.getStadium().getId() == null) {
            throw new ApiException("stadium id is required");
        }

        Stadium stadium = stadiumRepository.findStadiumById(match.getStadium().getId());

        if (stadium == null) {
            throw new ApiException("stadium not found");
        }

        return stadium;
    }

    private void validateMatch(Match match) {
        if (match.getStartTime() != null && match.getExpectedEndTime() != null
                && !match.getExpectedEndTime().isAfter(match.getStartTime())) {
            throw new ApiException("expected end time must be after start time");
        }

        if (match.getHomeTeam() != null && match.getAwayTeam() != null
                && match.getHomeTeam().trim().equalsIgnoreCase(match.getAwayTeam().trim())) {
            throw new ApiException("home team and away team must be different");
        }
    }
}
