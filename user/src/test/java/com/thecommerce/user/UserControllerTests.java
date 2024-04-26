package com.thecommerce.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thecommerce.user.user.UserController;
import com.thecommerce.user.user.UserRegistrationStatus;
import com.thecommerce.user.user.UserService;
import com.thecommerce.user.user.userDTO.UserDTO;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    void testJoinSuccess() throws Exception {
        UserDTO userDTO = new UserDTO(null, "userName", "test@example.com", "Password!123", "010-1234-5678", null);
        ObjectMapper objectMapper = new ObjectMapper();
        String userDTOJson = objectMapper.writeValueAsString(userDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/user/join")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userDTOJson))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    void testJoinWithExistingEmail() throws Exception {
        UserDTO userDTO = new UserDTO(null, "userName", "existing@example.com", "Password!123", "010-1234-5678", null);
        ObjectMapper objectMapper = new ObjectMapper();
        String userDTOJson = objectMapper.writeValueAsString(userDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/user/join")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userDTOJson))
                .andExpect(MockMvcResultMatchers.status().isConflict());
    }

    @Test
    void testJoinWithInvalidEmail() throws Exception {
        UserDTO userDTO = new UserDTO(null, "userName", "invalid_email", "Password!123", "010-1234-5678", null);
        ObjectMapper objectMapper = new ObjectMapper();
        String userDTOJson = objectMapper.writeValueAsString(userDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/user/join")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userDTOJson))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void testInvalidPhoneNumberFormat() throws Exception {
        UserDTO userDTO = new UserDTO(null, "user2", "test2@example.com", "Password!123", "010-1234-56890", null);
        ObjectMapper objectMapper = new ObjectMapper();
        String userDTOJson = objectMapper.writeValueAsString(userDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/user/join")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userDTOJson))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void testInvalidPasswordFormat() throws Exception {
        UserDTO userDTO = new UserDTO(null, "user3", "test3@example.com", "passwordinvalid", "010-1234-5689", null);
        ObjectMapper objectMapper = new ObjectMapper();
        String userDTOJson = objectMapper.writeValueAsString(userDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/user/join")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userDTOJson))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void testPasswordLengthTooShort() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setUserName("user1");
        userDTO.setEmail("user1@email.com");
        userDTO.setPhoneNumber("010-1234-568");
        String passwordTooShort = "Pass!1";
        userDTO.setPassword(passwordTooShort);
        ObjectMapper objectMapper = new ObjectMapper();
        String userDTOJson = objectMapper.writeValueAsString(userDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/user/join")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userDTOJson))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void testUserNameLengthTooShort() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setUserName("1");
        userDTO.setEmail("user1@email.com");
        userDTO.setPhoneNumber("010-1234-568");
        userDTO.setPassword("Password!123");
        ObjectMapper objectMapper = new ObjectMapper();
        String userDTOJson = objectMapper.writeValueAsString(userDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/user/join")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userDTOJson))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void testUserNameLengthTooLong() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setUserName("username1username1");
        userDTO.setEmail("user1@email.com");
        userDTO.setPhoneNumber("010-1234-568");
        userDTO.setPassword("Password!123");
        ObjectMapper objectMapper = new ObjectMapper();
        String userDTOJson = objectMapper.writeValueAsString(userDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/user/join")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userDTOJson))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void testPasswordLengthTooLong() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setUserName("user1");
        userDTO.setEmail("user1@email.com");
        userDTO.setPhoneNumber("010-1234-568");
        StringBuilder passwordTooLong = new StringBuilder("Password!1");
        for (int i = 0; i < 502; i++) {
            passwordTooLong.append("Password!1");
        }
        userDTO.setPassword(passwordTooLong.toString());
        ObjectMapper objectMapper = new ObjectMapper();
        String userDTOJson = objectMapper.writeValueAsString(userDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/user/join")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userDTOJson))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void testEmailLengthTooLong() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setUserName("user1");
        userDTO.setPhoneNumber("010-1234-568");
        userDTO.setPassword("Password!123");
        StringBuilder emailTooLong = new StringBuilder("user1");
        for (int i = 0; i < 502; i++) {
            emailTooLong.append("user1");
        }
        emailTooLong.append("@email.com");
        userDTO.setEmail(emailTooLong.toString());
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
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail(email);
        userDTO.setUserName("testuser");
        userDTO.setPassword("Passw0rd!");
        userDTO.setPhoneNumber("010-1234-5678");

        when(userService.checkDuplicateEmail(email)).thenReturn(UserRegistrationStatus.ALREADY_EXIST_EMAIL);

        ResponseEntity<?> response = userController.join(userDTO);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("email exist", response.getBody());
    }

}