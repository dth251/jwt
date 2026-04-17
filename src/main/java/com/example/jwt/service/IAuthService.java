package com.example.jwt.service;

import com.example.jwt.dto.request.UserLogin;
import com.example.jwt.dto.response.JwtResponse;

public interface IAuthService {

    /**
     * Đăng nhập và trả về JWT token
     */
    JwtResponse login(UserLogin userLogin);
}
