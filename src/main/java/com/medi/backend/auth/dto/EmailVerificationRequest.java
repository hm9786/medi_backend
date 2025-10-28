package com.medi.backend.auth.dto;

import lombok.Data;

/**
 * 이메일 인증 코드 전송 요청 DTO
 */
@Data
public class EmailVerificationRequest {
    private String email;  // 인증 코드를 받을 이메일
}
