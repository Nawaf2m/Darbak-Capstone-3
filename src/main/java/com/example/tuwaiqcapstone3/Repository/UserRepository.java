package com.example.tuwaiqcapstone3.Repository;

import com.example.tuwaiqcapstone3.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    User findUserById(Integer id);

    List<User> findDistinctByMatches_Id(Integer matchId);

    User findUserByEmail(String email);

    User findUserByPhoneNumber(String phoneNumber);
}