package com.medi.backend.auth.mapper;

import com.medi.backend.auth.dto.EmailVerification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 인증 관련 Mapper (email_verifications 테이블 관리)
 */
@Mapper
public interface AuthMapper {
    
    /**
     * 이메일 인증 코드 저장
     */
    int insertVerification(EmailVerification verification);
    
    /**
     * 이메일과 코드로 인증 정보 조회 (만료되지 않은 것만)
     */
    EmailVerification findByEmailAndCode(@Param("email") String email, @Param("code") String code);
    
    /**
     * 이메일로 기존 인증 코드 삭제 (재전송 대비)
     */
    int deleteByEmail(@Param("email") String email);
}