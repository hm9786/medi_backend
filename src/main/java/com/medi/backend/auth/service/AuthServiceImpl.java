package com.medi.backend.auth.service;


import com.medi.backend.auth.dto.EmailVerification;
import com.medi.backend.auth.dto.RegisterRequest;
import com.medi.backend.auth.mapper.AuthMapper;
import com.medi.backend.user.dto.UserDTO;
import com.medi.backend.user.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

/**
 * 인증 서비스 구현체
 */
@Service
public class AuthServiceImpl implements AuthService {
    
    @Autowired
    private AuthMapper authMapper;  // 이메일 인증 DB 작업
    
    @Autowired
    private UserMapper userMapper;  // 사용자 정보 DB 작업
    
    @Autowired
    private PasswordEncoder passwordEncoder;  // 비밀번호 암호화 (BCrypt)
    
    @Autowired
    private EmailService emailService;  // 이메일 전송 (같은 패키지!)
    
    private static final String CHARACTERS = "0123456789";  // 인증 코드 문자 (숫자만)
    private static final int CODE_LENGTH = 6;               // 인증 코드 길이 (6자리)
    private static final int EXPIRATION_MINUTES = 5;        // 만료 시간 (5분)
    private final SecureRandom random = new SecureRandom(); // 보안 난수 생성기
    
    /**
     * 이메일 인증 코드 생성 및 전송
     */
    @Override
    public String sendVerificationCode(String email) {
        // 1. 기존 인증 코드 삭제 (재전송 대비)
        authMapper.deleteByEmail(email);
        
        // 2. 6자리 랜덤 코드 생성
        String code = generateCode();
        
        // 3. 인증 정보 객체 생성
        EmailVerification verification = new EmailVerification();
        verification.setEmail(email);
        verification.setCode(code);
        verification.setExpiresAt(LocalDateTime.now().plusMinutes(EXPIRATION_MINUTES));
        
        // 4. DB에 저장
        authMapper.insertVerification(verification);
        
        // 5. 이메일 전송 (선택사항 - 에러 발생해도 진행) //배포시 여기 부분 수정해야함 
        try {
            emailService.sendVerificationEmail(email, code);
            System.out.println("✅ 이메일 전송 성공: " + email);
        } catch (Exception e) {
            System.err.println("❌ 이메일 전송 실패: " + e.getMessage());
            // 개발 중에는 콘솔로 확인하므로 에러 무시
        }
        
        // 6. 생성된 코드 반환 (실제로는 이메일 확인)
        return code;
    }
    
    /**
     * 이메일 인증 코드 검증
     */
    @Override
    public boolean verifyCode(String email, String code) {
        // 1. DB에서 유효한 인증 정보 조회 (만료 시간 자동 체크)
        EmailVerification verification = authMapper.findByEmailAndCode(email, code);
        
        // 2. 조회 실패 시 (코드 틀림 or 만료됨)
        if (verification == null) {
            return false;
        }
        
        // 3. 인증 성공 후 코드 삭제 (재사용 방지)
        authMapper.deleteByEmail(email);
        
        return true;
    }
    
    /**
     * 회원가입 처리
     */
    @Override
    public UserDTO register(RegisterRequest request) {
        // 1. 비밀번호 암호화 (BCrypt)
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        
        // 2. UserDTO 객체 생성
        UserDTO user = new UserDTO();
        user.setEmail(request.getEmail());
        user.setPassword(encodedPassword);  // 암호화된 비밀번호
        user.setName(request.getName());
        user.setPhone(request.getPhone());
        user.setIsTermsAgreed(request.getIsTermsAgreed());
        user.setRole("USER");  // 기본 역할
        
        // 3. DB에 사용자 정보 저장
        userMapper.insertUser(user);
        
        // 4. 저장된 사용자 정보 반환 (id 포함)
        return user;
    }
    
    /**
     * 이메일 중복 체크
     */
    @Override
    public boolean isEmailExists(String email) {
        return userMapper.existsByEmail(email) > 0;
    }
    
    /**
     * 6자리 랜덤 인증 코드 생성 (private 메서드)
     */
    private String generateCode() {
        StringBuilder code = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return code.toString();
    }
}