package com.medi.backend.user.service;

import com.medi.backend.user.dto.UserDTO;
import java.util.List;

public interface UserService {
    List<UserDTO> getAllUsers();

}