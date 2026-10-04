package com.example.tuwaiqcapstone3.Controller;

import com.example.tuwaiqcapstone3.API.ApiResponse;
import com.example.tuwaiqcapstone3.Model.Admin;
import com.example.tuwaiqcapstone3.Service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/get")
    public ResponseEntity<?> getAllAdmins() {
        return ResponseEntity.status(200).body(adminService.getAllAdmins());
    }

    @PostMapping("/add")
    public ResponseEntity<?> addAdmin(@RequestBody @Valid Admin admin) {
        adminService.addAdmin(admin);
        return ResponseEntity.status(200).body(new ApiResponse("Admin added successfully"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateAdmin(@PathVariable Integer id, @RequestBody @Valid Admin admin) {
        adminService.updateAdmin(id, admin);
        return ResponseEntity.status(200).body(new ApiResponse("Admin updated successfully"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteAdmin(@PathVariable Integer id) {
        adminService.deleteAdmin(id);
        return ResponseEntity.status(200).body(new ApiResponse("Admin deleted successfully"));
    }
}
