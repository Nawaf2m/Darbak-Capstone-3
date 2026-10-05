package com.example.tuwaiqcapstone3.Repository;

import com.example.tuwaiqcapstone3.Model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    User findUserById(Integer id);

    List<User> findDistinctByMatches_Id(Integer matchId);

    User findUserByEmail(String email);

    User findUserByPhoneNumber(String phoneNumber);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    List<User> findUsersByNameContainingIgnoreCase(String name);

    @Query("select u from User u where u.id in (" +
            "select r.reviewedUser.id from Review r group by r.reviewedUser.id " +
            "having avg(r.rating) > (select avg(r2.rating) from Review r2))")
    List<User> findUsersAboveAverageRating();

    @Query("select u from User u where u.id in (" +
            "select r.reviewedUser.id from Review r group by r.reviewedUser.id " +
            "having avg(r.rating) < (select avg(r2.rating) from Review r2))")
    List<User> findUsersBelowAverageRating();


}