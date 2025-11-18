package com.social.backend.config;
import com.social.backend.service.impl.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. Lấy header Authorization
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        // 2. Kiểm tra xem header có bắt đầu bằng "Bearer " không
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response); // Không có token thì cứ cho qua (để SecurityConfig chặn sau)
            return;
        }

        // 3. Cắt lấy token (bỏ chữ "Bearer " đi)
        jwt = authHeader.substring(7);

        // 4. Trích xuất username từ token
        // (Nếu token rác hoặc hết hạn, hàm này sẽ ném lỗi -> GlobalExceptionHandler sẽ bắt)
        username = jwtService.extractUsername(jwt);

        // 5. Nếu có username và chưa được xác thực trong Context hiện tại
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Lấy thông tin User từ DB
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // Kiểm tra token có hợp lệ với user này không
            if (jwtService.isTokenValid(jwt, userDetails.getUsername())) {

                // Tạo đối tượng Authentication
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // QUAN TRỌNG: Set user vào Context -> Spring biết user này đã đăng nhập
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 6. Cho phép request đi tiếp
        filterChain.doFilter(request, response);
    }
}
