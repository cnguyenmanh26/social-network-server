package com.social.backend.exception;



import lombok.Getter;

@Getter
public enum ErrorCode {
    // Định nghĩa các loại lỗi tại đây
    UNCATEGORIZED_EXCEPTION(9999, "Lỗi hệ thống chưa được định nghĩa"),
    INVALID_KEY(1001, "Sai message key"),
    USER_EXISTED(1002, "User đã tồn tại"),
    USERNAME_INVALID(1003, "Username phải có ít nhất 3 ký tự"),
    INVALID_PASSWORD(1004, "Password phải có ít nhất 8 ký tự"),
    EMAIL_EXISTED(1005, "Email đã được sử dụng"),
    USER_NOT_EXISTED(1006, "User không tồn tại"),
    UNAUTHENTICATED(1007, "Chưa đăng nhập"),
    INVALID_OTP(1008, "Mã OTP không chính xác hoặc đã hết hạn"),
    SPAM_OTP(1009, "Vui lòng đợi 60s trước khi gửi lại OTP");
    private int code;
    private String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
