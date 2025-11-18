package com.social.backend.dto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // Nếu field nào null thì không trả về json (cho gọn)
public class ApiResponse<T> {
    @Builder.Default
    private int code = 200; // Mặc định là 200 (OK)

    private String message;
    private T result;
}