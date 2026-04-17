package com.example.jwt.mapper;

import com.example.jwt.dto.request.UserRegister;
import com.example.jwt.entity.Role;
import com.example.jwt.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final PasswordEncoder passwordEncoder;

    public User toEntity(UserRegister dto, Set<Role> roles) {
        if (dto == null) {
            return null;
        }

        return User.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .fullName(dto.getFullName())
                .password(passwordEncoder.encode(dto.getPassword()))
                .status(true) // Mặc định status true khi đăng ký
                .roleSet(roles)
                .build();
    }
}
