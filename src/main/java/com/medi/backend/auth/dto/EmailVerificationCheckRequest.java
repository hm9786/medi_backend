package com.medi.backend.auth.dto;

import lombok.Data;

/**
 * 이메일 인증 코드 확인 요청 DTO
 */
@Data
public class EmailVerificationCheckRequest {
    private String email;  // 이메일
    private String code;   // 6자리 인증 코드
}
