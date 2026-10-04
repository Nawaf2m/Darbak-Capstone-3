package com.example.tuwaiqcapstone3.Service;

import com.example.tuwaiqcapstone3.API.ApiException;
import com.example.tuwaiqcapstone3.Model.Match;
import com.example.tuwaiqcapstone3.Model.Stadium;
import com.example.tuwaiqcapstone3.Repository.MatchRepository;
import com.example.tuwaiqcapstone3.Repository.StadiumRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MatchService {

    private final MatchRepository matchRepository;
    private final StadiumRepository stadiumRepository;

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
