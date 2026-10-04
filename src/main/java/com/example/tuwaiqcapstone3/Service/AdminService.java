package com.example.tuwaiqcapstone3.Service;

import com.example.tuwaiqcapstone3.API.ApiException;
import com.example.tuwaiqcapstone3.Model.Admin;
import com.example.tuwaiqcapstone3.Repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;

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

    public void deleteAdmin(Integer id) {
        Admin admin = adminRepository.findAdminById(id);
        if (admin == null) {
            throw new ApiException("Admin not found");
        }
        adminRepository.delete(admin);
    }


}
