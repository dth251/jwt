package com.example.jwt.security.principle;

import com.example.jwt.entity.User;
import com.example.jwt.exception.AppException;
import com.example.jwt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceCustom implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Tìm user theo username, email hoặc số điện thoại
        User user = userRepository.findByUsernameOrEmailOrPhone(username)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng: " + username));

        // Kiểm tra trạng thái tài khoản
        if (Boolean.FALSE.equals(user.getStatus())) {
            throw new AppException(HttpStatus.FORBIDDEN, "Tài khoản đã bị khóa");
        }

        // Chuyển đổi roles sang GrantedAuthority
        List<GrantedAuthority> authorities = user.getRoleSet().stream()
                .map(role -> new SimpleGrantedAuthority(role.getRoleName().name()))
                .collect(Collectors.toList());

        return UserDetailsCustom.builder()
                .id(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .fullName(user.getFullName())
                .authorities(authorities)
                .build();
    }
}
