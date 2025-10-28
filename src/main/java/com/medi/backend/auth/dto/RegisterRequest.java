package com.medi.backend.auth.dto;

import lombok.Data;

/**
 * 회원가입 요청 DTO
 */
@Data
public class RegisterRequest {
    private String email;           // 이메일
    private String password;        // 비밀번호 (평문)
    private String name;            // 이름
    private String phone;           // 전화번호
    private Boolean isTermsAgreed;  // 약관 동의 여부
}
