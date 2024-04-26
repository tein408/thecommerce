package com.thecommerce.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.thecommerce.user.user.User;
import com.thecommerce.user.user.UserRepository;
import com.thecommerce.user.user.UserRegistrationStatus;
import com.thecommerce.user.user.UserUpdateStatus;
import com.thecommerce.user.user.UserService;
import com.thecommerce.user.user.userDTO.UserDTO;
import com.thecommerce.user.user.userDTO.UserListDTO;
import com.thecommerce.user.user.userDTO.UpdateUserDTO;

@SpringBootTest
class UserServiceTests {

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @Test
    void testSaveUser() {
        UserDTO userDTO = new UserDTO(null, "userId", "user", "test@example.com", "Password!123", "010-1234-5678",
                null);

        UserRegistrationStatus result = userService.save(userDTO);

        assertEquals(UserRegistrationStatus.OK, result);
    }

    @Test
    void checkDuplicateEmailExistingEmailReturnsAlreadyExistEmail() {
        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.findUserByEmail("test@example.com")).thenReturn(Optional.of(new User()));
        UserService userService = new UserService(userRepository);

        UserRegistrationStatus result = userService.checkDuplicateEmail("test@example.com");

        assertEquals(UserRegistrationStatus.ALREADY_EXIST_EMAIL, result);
    }

    @Test
    void updateUserValidUserReturnsOK() {
        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.findUserByUserId("testUser")).thenReturn(Optional.of(new User()));
        UserService userService = new UserService(userRepository);

        UpdateUserDTO updateUserDTO = new UpdateUserDTO();
        updateUserDTO.setPassword("newPassword!123");
        updateUserDTO.setUserName("new");
        updateUserDTO.setPhoneNumber("010-5555-5555");

        UserUpdateStatus result = userService.updateUser(updateUserDTO, "testUser");

        assertEquals(UserUpdateStatus.OK, result);
    }

    @Test
    void getUserListReturnsPageOfUsers() {
        UserRepository userRepository = mock(UserRepository.class);
        List<User> userList = new ArrayList<>();
        userList.add(new User(1L, "user1", "User1", "user1@example.com", "010-1111-1111", null, LocalDateTime.now()));
        userList.add(new User(2L, "user2", "User2", "user2@example.com", "010-2222-2222", null, LocalDateTime.now()));
        Page<User> userPage = new PageImpl<>(userList);
        when(userRepository.findAll(any(Pageable.class))).thenReturn(userPage);

        UserService userService = new UserService(userRepository);

        Pageable pageable = Pageable.unpaged();

        Page<UserListDTO> result = userService.getUserList(pageable);

        assertEquals(2, result.getTotalElements());
        assertEquals("user1", result.getContent().get(0).getUserId());
        assertEquals("User2", result.getContent().get(1).getUserName());
    }

}