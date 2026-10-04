package com.example.tuwaiqcapstone3.Service;

import com.example.tuwaiqcapstone3.API.ApiException;
import com.example.tuwaiqcapstone3.Model.Match;
import com.example.tuwaiqcapstone3.Model.User;
import com.example.tuwaiqcapstone3.Model.UserMatch;
import com.example.tuwaiqcapstone3.Repository.MatchRepository;
import com.example.tuwaiqcapstone3.Repository.UserMatchRepository;
import com.example.tuwaiqcapstone3.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserMatchService {

    private final UserMatchRepository userMatchRepository;
    private final UserRepository userRepository;
    private final MatchRepository matchRepository;

    public List<UserMatch> getUserMatches() {
        return userMatchRepository.findAll();
    }

    public UserMatch getUserMatchById(Integer id) {
        UserMatch userMatch = userMatchRepository.findUserMatchById(id);

        if (userMatch == null) {
            throw new ApiException("user match not found");
        }

        return userMatch;
    }

    public void addUserMatch(UserMatch userMatch) {
        User user = getExistingUser(userMatch);
        Match match = getExistingMatch(userMatch);

        if (userMatchRepository.findUserMatchByUser_IdAndMatch_Id(user.getId(), match.getId()) != null) {
            throw new ApiException("user match already exists");
        }

        userMatch.setUser(user);
        userMatch.setMatch(match);
        userMatchRepository.save(userMatch);
    }

    public void updateUserMatch(Integer id, UserMatch userMatch) {
        UserMatch oldUserMatch = userMatchRepository.findUserMatchById(id);

        if (oldUserMatch == null) {
            throw new ApiException("user match not found");
        }

        User user = getExistingUser(userMatch);
        Match match = getExistingMatch(userMatch);
        UserMatch duplicate = userMatchRepository.findUserMatchByUser_IdAndMatch_Id(user.getId(), match.getId());

        if (duplicate != null && !duplicate.getId().equals(id)) {
            throw new ApiException("user match already exists");
        }

        oldUserMatch.setUser(user);
        oldUserMatch.setMatch(match);
        userMatchRepository.save(oldUserMatch);
    }

    public void deleteUserMatch(Integer id) {
        UserMatch userMatch = userMatchRepository.findUserMatchById(id);

        if (userMatch == null) {
            throw new ApiException("user match not found");
        }

        userMatchRepository.delete(userMatch);
    }

    private User getExistingUser(UserMatch userMatch) {
        if (userMatch.getUser() == null || userMatch.getUser().getId() == null) {
            throw new ApiException("user id is required");
        }

        User user = userRepository.findUserById(userMatch.getUser().getId());

        if (user == null) {
            throw new ApiException("user not found");
        }

        return user;
    }

    private Match getExistingMatch(UserMatch userMatch) {
        if (userMatch.getMatch() == null || userMatch.getMatch().getId() == null) {
            throw new ApiException("match id is required");
        }

        Match match = matchRepository.findMatchById(userMatch.getMatch().getId());

        if (match == null) {
            throw new ApiException("match not found");
        }

        return match;
    }
}
