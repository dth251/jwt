package com.example.jwt.config;

import com.example.jwt.entity.Role;
import com.example.jwt.entity.RoleName;
import com.example.jwt.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) {
        // Tạo các role mặc định nếu chưa có
        for (RoleName roleName : RoleName.values()) {
            if (roleRepository.findByRoleName(roleName).isEmpty()) {
                Role role = Role.builder().roleName(roleName).build();
                roleRepository.save(role);
                log.info("Đã khởi tạo role: {}", roleName);
            }
        }
        log.info("DataInitializer: Khởi tạo dữ liệu hoàn tất.");
    }
}
