package com.thecommerce.user.user.userDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserListDTO {
    private Long userIndex;
    private String userId;
    private String userName;
    private String email;
    private String phoneNumber;
    private LocalDateTime createDate;
}

