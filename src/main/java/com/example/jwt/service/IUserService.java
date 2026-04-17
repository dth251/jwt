package com.example.jwt.service;

import com.example.jwt.dto.request.UserRegister;
import com.example.jwt.dto.response.ApiResponse;
import com.example.jwt.entity.User;

import java.util.List;

public interface IUserService {

    /**
     * Đăng ký tài khoản mới
     */
    ApiResponse<User> register(UserRegister userRegister);

    /**
     * Lấy danh sách tất cả người dùng (ADMIN)
     */
    List<User> findAll();

    /**
     * Tìm user theo id
     */
    User findById(Long id);
}
