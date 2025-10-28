package com.medi.backend.user.service;

import com.medi.backend.user.dto.UserDTO;
import com.medi.backend.user.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserMapper userMapper;
    
    @Override
    public List<UserDTO> getAllUsers() {
        return userMapper.selectAllUsers();
    }
}