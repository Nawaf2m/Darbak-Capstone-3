package com.example.tuwaiqcapstone3.Repository;

import com.example.tuwaiqcapstone3.Model.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchRepository extends JpaRepository<Match, Integer> {

    Match findMatchById(Integer id);
}
