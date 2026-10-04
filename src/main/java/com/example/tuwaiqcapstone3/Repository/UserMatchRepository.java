package com.example.tuwaiqcapstone3.Repository;

import com.example.tuwaiqcapstone3.Model.UserMatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMatchRepository extends JpaRepository<UserMatch, Integer> {

    UserMatch findUserMatchById(Integer id);

    UserMatch findUserMatchByUser_IdAndMatch_Id(Integer userId, Integer matchId);
}
