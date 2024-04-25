package com.thecommerce.user.user;

import java.util.regex.Pattern;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thecommerce.user.user.userDTO.UserDTO;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService = null;

    /**
     * 회원가입 : 유효성 검사 로직 수행 후 회원가입 로직을 수행합니다.
     * 
     * @param UserDTO userDTO
     * @return 회원가입 성공시 HttpStatus.CREATED, 유효성 검사 실패시 HttpStatus.BAD_REQUEST를
     *         리턴합니다.
     */
    @Operation(summary = "회원가입", description = "회원가입 메서드입니다")
    @PostMapping(path = "/join")
    public ResponseEntity<?> join(@RequestBody UserDTO userDTO) {
        try {
            log.info("=============== user join start ===============");

            ResponseEntity<?> validationResponse = validateUser(userDTO);
            if (validationResponse.getStatusCode() != HttpStatus.OK) {
                return validationResponse;
            }

            ResponseEntity<?> saveResponse = saveUser(userDTO);
            if (saveResponse.getStatusCode() != HttpStatus.CREATED) {
                return saveResponse;
            }

            return new ResponseEntity<>("SUCCESS", HttpStatus.CREATED);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>("SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 전달된 userDTO를 통해 email, nickname, password가 유효한 값인지 확인합니다.
     * 
     * @param userDTO
     * @return email 혹은 nickname이 중복인 경우 HttpStatus.CONFLICT,
     *         email, nickname, password 길이가 맞지 않는 경우 HttpStatus.BAD_REQUEST
     *         비밀번호 조합이 맞지 않는 경우 HttpStatus.BAD_REQUEST를 리턴하고,
     *         모든 유효성이 통과한 경우 HttpStatus 200을 리턴합니다.
     */
    private ResponseEntity<?> validateUser(UserDTO userDTO) {
        String userEmail = userDTO.getEmail();
        if (!Pattern.matches("^[a-zA-Z0-9+-\\_.]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$", userEmail)) {
            return new ResponseEntity<>("email expression error", HttpStatus.BAD_REQUEST);
        }

        if (userEmail.length() > 500) {
            return new ResponseEntity<>("email length error", HttpStatus.BAD_REQUEST);
        }

        if (userService.selectEmail(userEmail).equals("exist")) {
            return new ResponseEntity<>("email exist", HttpStatus.CONFLICT);
        }

        String userName = userDTO.getUserName();
        if (userName.length() < 1 || 8 < userName.length()) {
            return new ResponseEntity<>("userName length error", HttpStatus.BAD_REQUEST);
        }

        if (userService.selectUserName(userName).equals("exist")) {
            return new ResponseEntity<>("userName exist", HttpStatus.CONFLICT);
        }

        if (userDTO.getPassword().length() < 8 || 500 < userDTO.getPassword().length()) {
            return new ResponseEntity<>("password length error", HttpStatus.BAD_REQUEST);
        }

        String regex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";
        if (Pattern.matches(regex, userDTO.getPassword())) {
            return new ResponseEntity<>("password combination error", HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok().build();
    }

    /**
     * userDTO를 입력받아 유저 정보를 database에 저장합니다.
     * 
     * @param UserDTO userDTO
     * @return 성공시 HttpStatus.CREATE, 서버 에러시 HttpStatus.INTERNAL_SERVER_ERROR를
     *         리턴합니다.
     */
    private ResponseEntity<?> saveUser(UserDTO userDTO) {
        String result = userService.save(userDTO);
        if (result.equals("ok")) {
            return new ResponseEntity<>("CREATED", HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>("SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
