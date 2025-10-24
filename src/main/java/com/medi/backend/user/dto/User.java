package com.medi.backend.user.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class User {
    private Integer id;
    private String email;
    private String password;
    private String name;
    private String phone;
    private Boolean isTermsAgreed;
    private String role;
    private String createdAt;
    private String updatedAt;
}