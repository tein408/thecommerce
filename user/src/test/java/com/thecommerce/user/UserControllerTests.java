package com.thecommerce.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thecommerce.user.user.userDTO.UserDTO;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testJoinSuccess() throws Exception {
        UserDTO userDTO = new UserDTO(null, "userName",  "test@example.com", "Password!123", null);
        ObjectMapper objectMapper = new ObjectMapper();
        String userDTOJson = objectMapper.writeValueAsString(userDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/user/join")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userDTOJson))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    void testJoinWithExistingEmail() throws Exception {
        UserDTO userDTO = new UserDTO(null, "userName",  "existing@example.com", "Password!123", null);
        ObjectMapper objectMapper = new ObjectMapper();
        String userDTOJson = objectMapper.writeValueAsString(userDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/user/join")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userDTOJson))
                .andExpect(MockMvcResultMatchers.status().isConflict());
    }

    @Test
    void testJoinWithInvalidEmail() throws Exception {
        UserDTO userDTO = new UserDTO(null, "userName", "invalid_email", "Password!123", null);
        ObjectMapper objectMapper = new ObjectMapper();
        String userDTOJson = objectMapper.writeValueAsString(userDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/user/join")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userDTOJson))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}