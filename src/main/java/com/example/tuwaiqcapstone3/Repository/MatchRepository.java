package com.example.tuwaiqcapstone3.Repository;

import com.example.tuwaiqcapstone3.Model.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchRepository extends JpaRepository<Match, Integer> {
    Match findMatchById(Integer id);

    List<Match> findMatchesByStadium_Id(Integer stadiumId);

    List<Match> findMatchesByHomeTeamIgnoreCaseOrAwayTeamIgnoreCase(String homeTeam, String awayTeam);

    List<Match> findMatchesByStadium_CityIgnoreCase(String city);

    List<Match> findDistinctMatchesByUsers_Id(Integer userId);

    Match findMatchByExternalFixtureId(Integer externalFixtureId);
}
