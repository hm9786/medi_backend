package com.medi.backend.auth.service;

import com.medi.backend.auth.dto.RegisterRequest;
import com.medi.backend.user.dto.UserDTO;

public interface AuthService {
        /**
     * 이메일 인증 코드 생성 및 전송
     */
    String sendVerificationCode(String email);
    
    /**
     * 이메일 인증 코드 검증
     */
    boolean verifyCode(String email, String code);
    
    /**
     * 회원가입 처리
     */
    UserDTO register(RegisterRequest request);
    
    /**
     * 이메일 중복 체크
     */
    boolean isEmailExists(String email);
}

