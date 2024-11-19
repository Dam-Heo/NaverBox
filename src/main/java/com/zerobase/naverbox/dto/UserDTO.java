package com.zerobase.naverbox.dto;

import com.zerobase.naverbox.entity.UserRole;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO{
    private UserRole userRole;
    private String name;
    private String userId;

}
