package com.example.jwt.controller;

import com.example.jwt.dto.request.UserLogin;
import com.example.jwt.dto.request.UserRegister;
import com.example.jwt.dto.response.ApiResponse;
import com.example.jwt.dto.response.JwtResponse;
import com.example.jwt.entity.User;
import com.example.jwt.service.IAuthService;
import com.example.jwt.service.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IAuthService authService;
    private final IUserService userService;

    /**
     * Đăng nhập
     * POST /api/v1/auth/sign-in
     */
    @PostMapping("/sign-in")
    public ResponseEntity<ApiResponse<JwtResponse>> login(@Valid @RequestBody UserLogin userLogin) {
        JwtResponse jwtResponse = authService.login(userLogin);
        return ResponseEntity.ok(ApiResponse.success("Đăng nhập thành công", jwtResponse));
    }

    /**
     * Đăng ký tài khoản mới
     * POST /api/v1/auth/sign-up
     */
    @PostMapping("/sign-up")
    public ResponseEntity<ApiResponse<User>> register(@Valid @RequestBody UserRegister userRegister) {
        ApiResponse<User> response = userService.register(userRegister);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
