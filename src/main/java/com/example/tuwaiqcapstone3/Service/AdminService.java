package com.example.tuwaiqcapstone3.Service;

import com.example.tuwaiqcapstone3.API.ApiException;
import com.example.tuwaiqcapstone3.Model.Admin;
import com.example.tuwaiqcapstone3.Model.User;
import com.example.tuwaiqcapstone3.Repository.AdminRepository;
import com.example.tuwaiqcapstone3.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;
    private final UserRepository userRepository;

    public List<Admin> getAllAdmins() {
        return adminRepository.findAll();
    }

    public void addAdmin(Admin admin) {
        if (adminRepository.existsByEmail(admin.getEmail())) {
            throw new ApiException("Email is already used");
        }
        if (adminRepository.existsByPhone(admin.getPhone())) {
            throw new ApiException("Phone number is already used");
        }
        adminRepository.save(admin);
    }

    public void updateAdmin(Integer id, Admin updatedAdmin) {
        Admin oldAdmin = adminRepository.findAdminById(id);
        if (oldAdmin == null) {
            throw new ApiException("Admin not found");
        }
        if (adminRepository.existsByEmailAndIdNot(updatedAdmin.getEmail(), id)) {
            throw new ApiException("Email is already used");
        }
        if (adminRepository.existsByPhoneAndIdNot(updatedAdmin.getPhone(), id)) {
            throw new ApiException("Phone number is already used");
        }

        oldAdmin.setName(updatedAdmin.getName());
        oldAdmin.setEmail(updatedAdmin.getEmail());
        oldAdmin.setPassword(updatedAdmin.getPassword());
        oldAdmin.setPhone(updatedAdmin.getPhone());
        adminRepository.save(oldAdmin);
    }

    // Marks a user as banned so they cannot log in.
    public void banUser(Integer userId) {
        User user = userRepository.findUserById(userId);

        if (user == null) {
            throw new ApiException("User not found");
        }

        if (user.getBanned()) {
            throw new ApiException("User is already banned");
        }

        user.setBanned(true);
        userRepository.save(user);
    }

    public void deleteAdmin(Integer id) {
        Admin admin = adminRepository.findAdminById(id);
        if (admin == null) {
            throw new ApiException("Admin not found");
        }
        adminRepository.delete(admin);
    }


}
