package com.social.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users") // Phải đúng tên bảng trong SQL
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "full_name")
    private String fullName;

    private String bio;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    private String status;
    private String gender;
    private LocalDate dob; // Import java.time.LocalDate
    private String location;
    private String website;
    private String coverUrl;
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Sau này phân quyền Admin/User thì sửa ở đây.
        // Tạm thời mặc định ai cũng là USER.
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return passwordHash; // Tr mapping passwordHash với getPassword của Spring
    }

    @Override
    public String getUsername() {
        return username;
    }

    // Các hàm dưới đây kiểm soát việc khóa tk, hết hạn tk.
    // Tạm thời cứ để true hết (User luôn sẵn sàng).
    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return true; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled() {
        return "ACTIVE".equals(status); // Chỉ enable khi status là ACTIVE
    }
}