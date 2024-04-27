package com.thecommerce.user.user.userDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long userIndex;
    private String userId;
    private String userName;
    private String email;
    private String password;
    private String phoneNumber;
    private LocalDateTime createDate;
}
