package com.thecommerce.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.AfterEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thecommerce.user.user.User;
import com.thecommerce.user.user.UserController;
import com.thecommerce.user.user.UserService;
import com.thecommerce.user.user.status.UserRegistrationStatus;
import com.thecommerce.user.user.UserRepository;
import com.thecommerce.user.user.userDTO.UserDTO;
import com.thecommerce.user.user.userDTO.UserListDTO;
import com.thecommerce.user.user.userDTO.UpdateUserDTO;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    public void setup() {
        user = new User();
        user.setUserId("initialUserId");
        user.setUserName("name");
        user.setEmail("initialUser@example.com");
        user.setPassword("Password!123");
        user.setPhoneNumber("010-1234-5678");
        userRepository.save(user);
    }

    @AfterEach
    public void tearDown() {
        userRepository.delete(user);
    }

    @Test
    void testJoinSuccess() throws Exception {
        UserDTO userDTO = new UserDTO(null, "userId", "userName", "test@example.com", "Password!123", "010-1234-5678",
                null);
        ObjectMapper objectMapper = new ObjectMapper();
        String userDTOJson = objectMapper.writeValueAsString(userDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/user/join")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userDTOJson))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    void testJoinWithExistingEmail() throws Exception {
        UserDTO userDTO = new UserDTO(null, "userId", "userName", "existing@example.com", "Password!123",
                "010-1234-5678", null);
        ObjectMapper objectMapper = new ObjectMapper();
        String userDTOJson = objectMapper.writeValueAsString(userDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/user/join")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userDTOJson))
                .andExpect(MockMvcResultMatchers.status().isConflict());
    }

    @Test
    void testJoinWithInvalidEmail() throws Exception {
        UserDTO userDTO = new UserDTO(null, "userId", "userName", "invalid_email", "Password!123", "010-1234-5678",
                null);
        ObjectMapper objectMapper = new ObjectMapper();
        String userDTOJson = objectMapper.writeValueAsString(userDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/user/join")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userDTOJson))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void testInvalidPhoneNumberFormat() throws Exception {
        UserDTO userDTO = new UserDTO(null, "userId", "user2", "test2@example.com", "Password!123", "010-1234-56890",
                null);
        ObjectMapper objectMapper = new ObjectMapper();
        String userDTOJson = objectMapper.writeValueAsString(userDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/user/join")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userDTOJson))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @ParameterizedTest
    @ValueSource(strings = { "password", "invalidpassword", "Pass!1" })
    void testInvalidPasswordFormat(String password) throws Exception {
        UserDTO userDTO = new UserDTO(null, "userId", "user3", "test3@example.com", password, "010-1234-5689",
                null);
        ObjectMapper objectMapper = new ObjectMapper();
        String userDTOJson = objectMapper.writeValueAsString(userDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/user/join")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userDTOJson))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @ParameterizedTest
    @ValueSource(strings = { "a", "userIduserIduserIduserIduserIduserIduserId" })
    void testInvalidUserIdLength(String userId) throws Exception {
        UserDTO userDTO = new UserDTO(null, userId, "user1", "test@example.com", "Password!123", "010-1234-5678",
                null);
        ObjectMapper objectMapper = new ObjectMapper();
        String userDTOJson = objectMapper.writeValueAsString(userDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/user/join")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userDTOJson))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @ParameterizedTest
    @ValueSource(strings = { "a", "username1username1" })
    void testInvalidUserNameLength(String userName) throws Exception {
        UserDTO userDTO = new UserDTO(null, "userId", userName, "test@example.com", "Password!123", "010-1234-5678",
                null);
        ObjectMapper objectMapper = new ObjectMapper();
        String userDTOJson = objectMapper.writeValueAsString(userDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/user/join")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userDTOJson))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @DisplayName("비밀번호는 500자 이상인 경우 에러를 반환한다.")
    void testPasswordLengthTooLong() throws Exception {
        StringBuilder passwordTooLong = new StringBuilder("Password!1");
        for (int i = 0; i < 491; i++) {
            passwordTooLong.append(1);
        }
        UserDTO userDTO = new UserDTO(null, "userId", "user1", "test1@example.com", passwordTooLong.toString(),
                "010-1234-5678",
                null);
        ObjectMapper objectMapper = new ObjectMapper();
        String userDTOJson = objectMapper.writeValueAsString(userDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/user/join")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userDTOJson))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @DisplayName("이메일은 500자 이상인 경우 에러를 반환한다.")
    void testEmailLengthTooLong() throws Exception {
        StringBuilder emailTooLong = new StringBuilder("user1");
        for (int i = 0; i < 496; i++) {
            emailTooLong.append(1);
        }
        emailTooLong.append("@email.com");
        UserDTO userDTO = new UserDTO(null, "userId", "userName", emailTooLong.toString(), "Password!123",
                "010-1234-5678",
                null);
        ObjectMapper objectMapper = new ObjectMapper();
        String userDTOJson = objectMapper.writeValueAsString(userDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/user/join")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userDTOJson))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void testDuplicateEmail() {
        String email = "test@example.com";
        UserDTO userDTO = new UserDTO(null, "userId", "userName", email, "Password!123", "010-1234-5678",
                null);
        when(userService.checkDuplicateEmail(email)).thenReturn(UserRegistrationStatus.ALREADY_EXIST_EMAIL);

        ResponseEntity<?> response = userController.join(userDTO);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("email exist", response.getBody());
    }

    @Test
    void testUpdateUserInfoSuccess() throws Exception {
        UpdateUserDTO updateUserDTO = new UpdateUserDTO();
        updateUserDTO.setPassword("newPassword!123");
        updateUserDTO.setUserName("new");
        updateUserDTO.setPhoneNumber("010-1234-1234");
        ObjectMapper objectMapper = new ObjectMapper();
        String updateUserDTOJson = objectMapper.writeValueAsString(updateUserDTO);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/user/initialUserId")
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateUserDTOJson))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @ParameterizedTest
    @ValueSource(strings = { "a", "username1username1" })
    void testUpdateInvalidUserNameUserInfo(String userName) throws Exception {
        UpdateUserDTO updateUserDTO = new UpdateUserDTO();
        updateUserDTO.setUserName(userName);
        ObjectMapper objectMapper = new ObjectMapper();
        String updateUserDTOJson = objectMapper.writeValueAsString(updateUserDTO);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/user/initialUserId")
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateUserDTOJson))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void testUpdateDuplicateUserNameUserInfo() throws Exception {
        UpdateUserDTO updateUserDTO = new UpdateUserDTO();
        updateUserDTO.setUserName("name");
        ObjectMapper objectMapper = new ObjectMapper();
        String updateUserDTOJson = objectMapper.writeValueAsString(updateUserDTO);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/user/initialUserId")
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateUserDTOJson))
                .andExpect(MockMvcResultMatchers.status().isConflict());
    }

    @ParameterizedTest
    @ValueSource(strings = { "Pass!1", "password", "username1username1" })
    void testUpdateTooShortPasswordUserInfo(String password) throws Exception {
        UpdateUserDTO updateUserDTO = new UpdateUserDTO();
        updateUserDTO.setPassword(password);
        ObjectMapper objectMapper = new ObjectMapper();
        String updateUserDTOJson = objectMapper.writeValueAsString(updateUserDTO);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/user/initialUserId")
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateUserDTOJson))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void testUpdateInvalidPhoneNumberFormatUserInfo() throws Exception {
        UpdateUserDTO updateUserDTO = new UpdateUserDTO();
        updateUserDTO.setPhoneNumber("010-1234-56780");
        ObjectMapper objectMapper = new ObjectMapper();
        String updateUserDTOJson = objectMapper.writeValueAsString(updateUserDTO);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/user/initialUserId")
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateUserDTOJson))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void testUpdateInvalidUserInfo() throws Exception {
        UpdateUserDTO updateUserDTO = new UpdateUserDTO();
        updateUserDTO.setPhoneNumber("010-1234-1234");
        ObjectMapper objectMapper = new ObjectMapper();
        String updateUserDTOJson = objectMapper.writeValueAsString(updateUserDTO);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/user/invalidUserId")
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateUserDTOJson))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void testGetUserListSuccess() throws Exception {
        List<UserListDTO> userList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            userList.add(
                    new UserListDTO(
                            (long) i,
                            "user" + i,
                            "user" + i,
                            "email" + i + "@example.com",
                            "010-1234-5678",
                            LocalDateTime.now()));
        }
        Page<UserListDTO> userPage = new PageImpl<>(userList);
        when(userService.getUserList(PageRequest.of(0, 10)))
                .thenReturn(userPage);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/user/list")
                .param("page", "1")
                .param("size", "10"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void testGetUserListSortByUserName() throws Exception {
        userRepository.deleteAll();
        for (int i = 10; i < 30; i++) {
            User setUpUser = new User();
            setUpUser.setUserId("list" + i);
            setUpUser.setUserName("list" + i);
            setUpUser.setEmail("list" + i + "@example.com");
            setUpUser.setPassword("Password!123");
            setUpUser.setPhoneNumber("010-1234-5678");
            setUpUser.setCreateDate(LocalDateTime.now());
            userRepository.save(setUpUser);
        }

        // Assert: API에서 회원이름 순으로 정렬하여 10개씩 조회한 2페이지에는 20번대 유저가 나와야한다.
        mockMvc.perform(MockMvcRequestBuilders.get("/api/user/list")
                .param("page", String.valueOf(1))
                .param("pageSize", String.valueOf(10))
                .param("userNameSort", "userNameSort"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content", hasSize(10)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].userName", is("list20")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[1].userName", is("list21")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[2].userName", is("list22")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[9].userName", is("list29")));
    }

    @Test
    void testGetUserListSortByCreateDate() throws Exception {
        userRepository.deleteAll();
        LocalDateTime now = LocalDateTime.now().minusDays(40);
        for (int i = 10; i < 30; i++) {
            User setUpUser = new User();
            setUpUser.setUserId("list" + i);
            setUpUser.setUserName("list" + i);
            setUpUser.setEmail("list" + i + "@example.com");
            setUpUser.setPassword("Password!123");
            setUpUser.setPhoneNumber("010-1234-5678");
            // 고정된 시간으로 생성일을 설정
            setUpUser.setCreateDate(now.plusDays(i));
            userRepository.save(setUpUser);
        }

        // Assert: API에서 가입일 순으로 정렬하여 10개씩 조회한 2페이지에는 10번대 유저가 역순으로 나와야한다.
        mockMvc.perform(MockMvcRequestBuilders.get("/api/user/list")
                .param("page", String.valueOf(1))
                .param("pageSize", String.valueOf(10))
                .param("createDateSort", "createDateSort"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content", hasSize(10)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].userName", is("list19")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[1].userName", is("list18")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[2].userName", is("list17")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[9].userName", is("list10")));
    }

    @Test
    void testGetUserListSortByCreateDateAndUserName() throws Exception {
        userRepository.deleteAll();
        LocalDateTime now = LocalDateTime.now().minusDays(40);
        for (int i = 10; i < 30; i++) {
            User setUpUser = new User();
            setUpUser.setUserId("list" + i);
            setUpUser.setUserName("list" + i);
            setUpUser.setEmail("list" + i + "@example.com");
            setUpUser.setPassword("Password!123");
            setUpUser.setPhoneNumber("010-1234-5678");
            // 고정된 시간으로 생성일을 설정
            setUpUser.setCreateDate(now.plusDays(i));
            userRepository.save(setUpUser);
        }

        // Assert: API에서 가입일 순과 회원이름순으로 정렬하여 10개씩 조회한 2페이지에는 10번대 유저가 역순으로 나와야한다.
        mockMvc.perform(MockMvcRequestBuilders.get("/api/user/list")
                .param("page", String.valueOf(1))
                .param("pageSize", String.valueOf(10))
                .param("createDateSort", "createDateSort")
                .param("userNameSort", "userNameSort"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content", hasSize(10)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].userName", is("list19")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[1].userName", is("list18")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[2].userName", is("list17")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[9].userName", is("list10")));
    }

    @Test
    void testSaveUserFail() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        UserDTO userDTO = new UserDTO();
        String userDTOJson = objectMapper.writeValueAsString(userDTO);

        when(userService.save(userDTO)).thenReturn(UserRegistrationStatus.FAIL);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/user/join")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userDTOJson))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError());
    }

}