package com.example.jwt.repository;

import com.example.jwt.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Tìm user theo username, email hoặc phone (dùng cho đăng nhập)
     */
    @Query("SELECT u FROM User u WHERE u.username = :value OR u.email = :value OR u.phone = :value")
    Optional<User> findByUsernameOrEmailOrPhone(@Param("value") String value);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);
}
