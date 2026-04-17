package com.example.jwt.service.impl;

import com.example.jwt.dto.request.UserRegister;
import com.example.jwt.dto.response.ApiResponse;
import com.example.jwt.entity.Role;
import com.example.jwt.entity.RoleName;
import com.example.jwt.entity.User;
import com.example.jwt.exception.AppException;
import com.example.jwt.repository.RoleRepository;
import com.example.jwt.repository.UserRepository;
import com.example.jwt.service.IUserService;
import com.example.jwt.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;

    @Override
    public ApiResponse<User> register(UserRegister userRegister) {
        // Kiểm tra username đã tồn tại
        if (userRepository.existsByUsername(userRegister.getUsername())) {
            throw new AppException(HttpStatus.CONFLICT, "Username đã tồn tại");
        }
        // Kiểm tra email đã tồn tại
        if (userRepository.existsByEmail(userRegister.getEmail())) {
            throw new AppException(HttpStatus.CONFLICT, "Email đã tồn tại");
        }
        // Kiểm tra phone nếu có
        if (userRegister.getPhone() != null && !userRegister.getPhone().isBlank()
                && userRepository.existsByPhone(userRegister.getPhone())) {
            throw new AppException(HttpStatus.CONFLICT, "Số điện thoại đã tồn tại");
        }

        // Lấy role mặc định USER
        Role userRole = roleRepository.findByRoleName(RoleName.USER)
                .orElseThrow(() -> new AppException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Role USER chưa được khởi tạo trong database"));

        Set<Role> roles = new HashSet<>();
        roles.add(userRole);

        User user = userMapper.toEntity(userRegister, roles);

        User saved = userRepository.save(user);
        return ApiResponse.success("Đăng ký thành công", saved);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND,
                        "Không tìm thấy người dùng với id: " + id));
    }
}
