package com.example.jwt.controller;

import com.example.jwt.dto.response.ApiResponse;
import com.example.jwt.entity.User;
import com.example.jwt.security.principle.UserDetailsCustom;
import com.example.jwt.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    /**
     * Xem thông tin profile của chính mình (USER đã đăng nhập)
     * GET /api/v1/user/profile
     */
    @GetMapping("/user/profile")
    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN') or hasAuthority('MANAGER')")
    public ResponseEntity<ApiResponse<User>> getProfile(
            @AuthenticationPrincipal UserDetailsCustom currentUser) {
        User user = userService.findById(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Lấy thông tin thành công", user));
    }

    /**
     * Lấy danh sách toàn bộ người dùng – chỉ ADMIN
     * GET /api/v1/admin/users
     */
    @GetMapping("/admin/users")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
        List<User> users = userService.findAll();
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách người dùng thành công", users));
    }

    /**
     * Xem thông tin user theo id – chỉ ADMIN
     * GET /api/v1/admin/users/{id}
     */
    @GetMapping("/admin/users/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<User>> getUserById(@PathVariable Long id) {
        User user = userService.findById(id);
        return ResponseEntity.ok(ApiResponse.success("Lấy thông tin người dùng thành công", user));
    }

    /**
     * Endpoint dành cho MANAGER
     * GET /api/v1/manager/dashboard
     */
    @GetMapping("/manager/dashboard")
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity<ApiResponse<String>> managerDashboard() {
        return ResponseEntity.ok(ApiResponse.success("Chào mừng đến Manager Dashboard", "Manager area"));
    }
}
