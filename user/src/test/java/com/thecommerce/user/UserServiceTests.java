package com.thecommerce.user;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.thecommerce.user.user.UserRepository;
import com.thecommerce.user.user.UserService;
import com.thecommerce.user.user.userDTO.UserDTO;
import com.thecommerce.user.user.UserRegistrationStatus;

@SpringBootTest
class UserServiceTests {

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @Test
    void testSaveUser() {
        UserDTO userDTO = new UserDTO(null, "userId", "test_user", "test@example.com", "password123", "010-1234-5678", null);

        UserRegistrationStatus result = userService.save(userDTO);
        assertEquals(UserRegistrationStatus.OK, result);
    }

}