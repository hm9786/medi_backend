package com.medi.backend.user.mapper;

import com.medi.backend.user.dto.User;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface UserMapper {
    // 전체 사용자 조회
    List<User> selectAllUsers();
    
}