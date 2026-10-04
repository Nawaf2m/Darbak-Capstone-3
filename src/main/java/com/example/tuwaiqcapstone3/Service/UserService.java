package com.example.tuwaiqcapstone3.Service;

import com.example.tuwaiqcapstone3.API.ApiException;
import com.example.tuwaiqcapstone3.Model.User;
import com.example.tuwaiqcapstone3.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

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

    public void deleteUser(Integer id) {

        User user = userRepository.findUserById(id);

        if (user == null) {
            throw new ApiException("user not found");
        }

        userRepository.delete(user);
    }
}