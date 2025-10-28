package com.medi.backend.auth.controller;

import com.medi.backend.auth.dto.EmailVerificationCheckRequest;
import com.medi.backend.auth.dto.EmailVerificationRequest;
import com.medi.backend.auth.dto.RegisterRequest;
import com.medi.backend.auth.service.AuthService;
import com.medi.backend.user.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 인증 관련 API 컨트롤러
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    /**
     * 1단계: 이메일 인증 코드 전송
     * POST /api/auth/send-verification
     */
    @PostMapping("/send-verification")
    public ResponseEntity<?> sendVerificationCode(@RequestBody EmailVerificationRequest request) {
        try {
            String email = request.getEmail();
            
            // 1. 이메일 유효성 검증
            if (email == null || email.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(createErrorResponse("이메일을 입력해주세요"));
            }
            
            // 2. 이메일 중복 체크
            if (authService.isEmailExists(email)) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(createErrorResponse("이미 가입된 이메일입니다"));
            }
            
            // 3. 인증 코드 생성 및 저장
            String code = authService.sendVerificationCode(email);
            
            // (실제 배포 시 제거)
            // ========== 여기부터 삭제 ========== ❌
            // 4. 콘솔 출력 (MVP용 - 실제로는 이메일 전송)
            System.out.println("================================");
            System.out.println("📧 이메일 인증 코드 전송");
            System.out.println("수신: " + email);
            System.out.println("인증 코드: " + code);
            System.out.println("유효 시간: 5분");
            System.out.println("================================");
            // ========== 여기까지 삭제 ========== ❌
            
            // 5. 성공 응답
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "인증 코드가 이메일로 전송되었습니다");
            response.put("email", email);
            response.put("expiresIn", 300);  // 5분 (초 단위)
            response.put("code", code);       // <= MVP용 (실제 배포 시 제거)
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("인증 코드 전송 실패"));
        }
    }
    
    /**
     * 2단계: 이메일 인증 코드 확인
     * POST /api/auth/verify-email
     */
    @PostMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestBody EmailVerificationCheckRequest request) {
        try {
            String email = request.getEmail();
            String code = request.getCode();
            
            // 1. 입력값 검증
            if (email == null || email.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(createErrorResponse("이메일을 입력해주세요"));
            }
            
            if (code == null || code.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(createErrorResponse("인증 코드를 입력해주세요"));
            }
            
            // 2. 인증 코드 검증
            boolean isValid = authService.verifyCode(email, code);
            
            // 3. 결과에 따라 응답
            if (isValid) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "이메일 인증이 완료되었습니다");
                response.put("verified", true);
                
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse("인증 코드가 올바르지 않거나 만료되었습니다"));
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("인증 확인 실패"));
        }
    }
    
    /**
     * 3단계: 회원가입
     * POST /api/auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            // 1. 입력값 유효성 검증
            if (request.getEmail() == null || request.getEmail().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(createErrorResponse("이메일을 입력해주세요"));
            }
            
            if (request.getPassword() == null || request.getPassword().length() < 8) {
                return ResponseEntity.badRequest()
                    .body(createErrorResponse("비밀번호는 8자 이상이어야 합니다"));
            }
            
            if (request.getName() == null || request.getName().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(createErrorResponse("이름을 입력해주세요"));
            }
            
            if (request.getPhone() == null || request.getPhone().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(createErrorResponse("전화번호를 입력해주세요"));
            }
            
            if (request.getIsTermsAgreed() == null || !request.getIsTermsAgreed()) {
                return ResponseEntity.badRequest()
                    .body(createErrorResponse("약관에 동의해주세요"));
            }
            
            // 2. 이메일 중복 체크 (재확인)
            if (authService.isEmailExists(request.getEmail())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(createErrorResponse("이미 가입된 이메일입니다"));
            }
            
            // 3. 회원가입 처리
            UserDTO user = authService.register(request);
            
            // 4. 성공 응답 (201 Created)
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "회원가입이 완료되었습니다");
            response.put("userId", user.getId());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("회원가입 처리 중 오류가 발생했습니다"));
        }
    }
    
    /**
     * 에러 응답 생성 헬퍼 메서드
     */
    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        return response;
    }
}