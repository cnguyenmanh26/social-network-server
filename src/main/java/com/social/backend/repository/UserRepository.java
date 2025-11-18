package com.social.backend.repository;

import com.social.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Spring tự động hiểu: SELECT * FROM users WHERE username = ?
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    // Kiểm tra tồn tại: SELECT COUNT(*) > 0 ...
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}