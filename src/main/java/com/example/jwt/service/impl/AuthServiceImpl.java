package com.example.jwt.service.impl;

import com.example.jwt.dto.request.UserLogin;
import com.example.jwt.dto.response.JwtResponse;
import com.example.jwt.entity.User;
import com.example.jwt.exception.AppException;
import com.example.jwt.repository.UserRepository;
import com.example.jwt.security.jwt.JWTProvider;
import com.example.jwt.security.principle.UserDetailsCustom;
import com.example.jwt.service.IAuthService;
import com.example.jwt.mapper.AuthMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {

    private final AuthenticationManager authenticationManager;
    private final JWTProvider jwtProvider;
    private final UserRepository userRepository;
    private final AuthMapper authMapper;

    @Override
    public JwtResponse login(UserLogin userLogin) {
        try {
            // Xác thực credentials
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            userLogin.getUsername(),
                            userLogin.getPassword()
                    )
            );

            UserDetailsCustom userDetails = (UserDetailsCustom) authentication.getPrincipal();

            // Sinh JWT token
            String token = jwtProvider.generateToken(userDetails.getUsername());

            // Lấy danh sách roles
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            // Lấy thêm thông tin user từ DB
            User user = userRepository.findByUsernameOrEmailOrPhone(userDetails.getUsername())
                    .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "User không tồn tại"));

            return authMapper.toJwtResponse(userDetails, token, user, roles);

        } catch (Exception ex) {
            throw new AppException(HttpStatus.UNAUTHORIZED, "Tên đăng nhập hoặc mật khẩu không đúng");
        }
    }
}
