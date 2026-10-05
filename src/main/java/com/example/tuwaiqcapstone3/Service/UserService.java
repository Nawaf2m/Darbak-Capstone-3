package com.example.tuwaiqcapstone3.Service;

import com.example.tuwaiqcapstone3.API.ApiException;
import com.example.tuwaiqcapstone3.Model.Match;
import com.example.tuwaiqcapstone3.Model.User;
import com.example.tuwaiqcapstone3.Repository.MatchRepository;
import com.example.tuwaiqcapstone3.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final MatchRepository matchRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Integer id) {

        User user = userRepository.findUserById(id);

        if (user == null) {
            throw new ApiException("user not found");
        }

        return user;
    }

    public void addUser(User user) {

        User checkEmail = userRepository.findUserByEmail(user.getEmail());

        if (checkEmail != null) {
            throw new ApiException("email already exists");
        }

        User checkPhone = userRepository.findUserByPhoneNumber(user.getPhoneNumber());

        if (checkPhone != null) {
            throw new ApiException("phone number already exists");
        }

        user.setCreatedAt(LocalDateTime.now());

        userRepository.save(user);
    }

    public void updateUser(Integer id, User user) {

        User oldUser = userRepository.findUserById(id);

        if (oldUser == null) {
            throw new ApiException("user not found");
        }

        User checkEmail = userRepository.findUserByEmail(user.getEmail());

        if (checkEmail != null && !checkEmail.getId().equals(id)) {
            throw new ApiException("email already exists");
        }

        User checkPhone = userRepository.findUserByPhoneNumber(user.getPhoneNumber());

        if (checkPhone != null && !checkPhone.getId().equals(id)) {
            throw new ApiException("phone number already exists");
        }

        oldUser.setName(user.getName());
        oldUser.setEmail(user.getEmail());
        oldUser.setPassword(user.getPassword());
        oldUser.setPhoneNumber(user.getPhoneNumber());

        userRepository.save(oldUser);
    }

    @Transactional
    public void deleteUser(Integer id) {

        User user = userRepository.findUserById(id);

        if (user == null) {
            throw new ApiException("user not found");
        }

        user.getMatches().clear();
        userRepository.delete(user);
    }

    @Transactional(readOnly = true)
    public List<Match> getUserMatches(Integer userId) {
        User user = getUserById(userId);
        return new ArrayList<>(user.getMatches());
    }

    @Transactional
    public void addMatchToUser(Integer userId, Integer matchId) {
        User user = getUserById(userId);
        Match match = matchRepository.findMatchById(matchId);

        if (match == null) {
            throw new ApiException("match not found");
        }

        if (!user.getMatches().add(match)) {
            throw new ApiException("user match already exists");
        }

        match.getUsers().add(user);
        userRepository.save(user);
    }

    @Transactional
    public void removeMatchFromUser(Integer userId, Integer matchId) {
        User user = getUserById(userId);
        Match match = matchRepository.findMatchById(matchId);

        if (match == null) {
            throw new ApiException("match not found");
        }

        if (!user.getMatches().remove(match)) {
            throw new ApiException("user match not found");
        }

        match.getUsers().remove(user);
        userRepository.save(user);
    }
}