package com.example.jwt.mapper;

import com.example.jwt.dto.response.JwtResponse;
import com.example.jwt.entity.User;
import com.example.jwt.security.principle.UserDetailsCustom;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AuthMapper {

    public JwtResponse toJwtResponse(UserDetailsCustom userDetails, String token, User user, List<String> roles) {
        if (userDetails == null || user == null) {
            return null;
        }

        return JwtResponse.builder()
                .token(token)
                .type("Bearer")
                .id(userDetails.getId())
                .username(userDetails.getUsername())
                .fullName(userDetails.getFullName())
                .email(user.getEmail())
                .roles(roles)
                .build();
    }
}
