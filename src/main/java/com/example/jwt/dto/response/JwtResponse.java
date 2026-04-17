package com.example.jwt.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponse {

    @Builder.Default
    private String type = "Bearer";

    private String token;

    private Long id;
    private String username;
    private String fullName;
    private String email;
    private List<String> roles;
}
