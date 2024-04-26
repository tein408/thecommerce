package com.thecommerce.user.user;

import java.util.regex.Pattern;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.thecommerce.user.user.userDTO.UserDTO;
import com.thecommerce.user.user.userDTO.UserListDTO;
import com.thecommerce.user.user.userDTO.UpdateUserDTO;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    /**
     * 회원가입 : 유효성 검사 로직 수행 후 회원가입 로직을 수행합니다.
     * 
     * @param userDTO 회원 정보
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
     * 전달된 userDTO를 통해 email, nickname, password, phoneNumber가 유효한 값인지 확인합니다.
     * 
     * @param userDTO 회원 정보
     * @return email 혹은 nickname이 중복인 경우 HttpStatus.CONFLICT,
     *         email, nickname, password 길이가 맞지 않는 경우 HttpStatus.BAD_REQUEST
     *         비밀번호 조합이 맞지 않는 경우 HttpStatus.BAD_REQUEST를 리턴하고,
     *         모든 유효성이 통과한 경우 HttpStatus 200을 리턴합니다.
     */
    private ResponseEntity<?> validateUser(UserDTO userDTO) {
        String userId = userDTO.getUserId();
        if (userId.length() < 4 || userId.length() > 20) {
            return new ResponseEntity<>("user Id length error", HttpStatus.BAD_REQUEST);
        }

        String userEmail = userDTO.getEmail();
        if (!Pattern.matches("^[a-zA-Z0-9+-\\_.]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$", userEmail)) {
            return new ResponseEntity<>("email expression error", HttpStatus.BAD_REQUEST);
        }

        if (userEmail.length() > 500) {
            return new ResponseEntity<>("email length error", HttpStatus.BAD_REQUEST);
        }

        if (userService.checkDuplicateEmail(userEmail).equals(UserRegistrationStatus.ALREADY_EXIST_EMAIL)) {
            return new ResponseEntity<>("email exist", HttpStatus.CONFLICT);
        }

        String userName = userDTO.getUserName();
        if (userName.length() < 2 || 8 < userName.length()) {
            return new ResponseEntity<>("userName length error", HttpStatus.BAD_REQUEST);
        }

        if (userService.checkDuplicateUserName(userName).equals(UserRegistrationStatus.ALREADY_EXIST_USER_NAME)) {
            return new ResponseEntity<>("userName exist", HttpStatus.CONFLICT);
        }

        if (userDTO.getPassword().length() < 8 || 500 < userDTO.getPassword().length()) {
            return new ResponseEntity<>("password length error", HttpStatus.BAD_REQUEST);
        }

        String regex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])\\S{8,}$";
        if (!Pattern.matches(regex, userDTO.getPassword())) {
            return new ResponseEntity<>("password combination error", HttpStatus.BAD_REQUEST);
        }

        String phoneNumber = userDTO.getPhoneNumber();
        if (phoneNumber == null || !Pattern.matches("^\\d{2,3}-\\d{3,4}-\\d{4}$", phoneNumber)) {
            return new ResponseEntity<>("phone number format error", HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok().build();
    }

    /**
     * userDTO를 입력받아 유저 정보를 database에 저장합니다.
     * 
     * @param userDTO 회원 정보
     * @return 성공시 HttpStatus.CREATE, 서버 에러시 HttpStatus.INTERNAL_SERVER_ERROR를
     *         리턴합니다.
     */
    private ResponseEntity<?> saveUser(UserDTO userDTO) {
        UserRegistrationStatus result = userService.save(userDTO);
        if (result.equals(UserRegistrationStatus.OK)) {
            return new ResponseEntity<>("CREATED", HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>("SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 회원 아이디를 통해 userName, password, PhoneNumber를 수정합니다.
     *
     * @param userDTO 회원 정보
     * @param loginId 회원 아이디
     * @return 회원 정보 수정 성공 시 HttpStatus.OK, 유효성 검사 실패 시 HttpStatus.BAD_REQUEST,
     *         서버 에러 시 HttpStatus.INTERNAL_SERVER_ERROR를 반환합니다.
     */
    @Operation(summary = "회원 정보 수정", description = "회원 정보 수정 메서드입니다.")
    @PutMapping("/{loginId}")
    public ResponseEntity<?> updateUserInfo(@RequestBody UpdateUserDTO userDTO,
            @PathVariable("loginId") String loginId) {
        try {
            log.info("=============== user information update start ===============");

            ResponseEntity<?> validationResponse = validateUpdateUserInfo(userDTO);
            if (validationResponse.getStatusCode() != HttpStatus.OK) {
                return validationResponse;
            }

            UserUpdateStatus updateStatus = userService.updateUser(userDTO, loginId);
            if (updateStatus == UserUpdateStatus.OK) {
                return new ResponseEntity<>("User information updated successfully", HttpStatus.OK);
            } else if (updateStatus == UserUpdateStatus.INVALID_USER) {
                return new ResponseEntity<>("Invalid user", HttpStatus.BAD_REQUEST);
            } else {
                return new ResponseEntity<>("Server error", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>("SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 회원 정보 수정 시 userName, password, phoneNumber에 대해 유효성 검사를 합니다.
     *
     * @param userDTO 회원 정보
     * @return 유효성 검사 통과 시 HttpStatus.OK, 실패 시 적절한 HttpStatus와 에러 메시지를 반환합니다.
     */
    private ResponseEntity<?> validateUpdateUserInfo(UpdateUserDTO userDTO) {
        if (userDTO.getPassword() != null) {
            if (userDTO.getPassword().length() < 8 || 500 < userDTO.getPassword().length()) {
                return new ResponseEntity<>("password length error", HttpStatus.BAD_REQUEST);
            }

            String regex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])\\S{8,}$";
            if (!Pattern.matches(regex, userDTO.getPassword())) {
                return new ResponseEntity<>("password combination error", HttpStatus.BAD_REQUEST);
            }
        }

        if (userDTO.getUserName() != null) {
            if (userDTO.getUserName().length() < 2 || 8 < userDTO.getUserName().length()) {
                return new ResponseEntity<>("userName length error", HttpStatus.BAD_REQUEST);
            }

            if (userService.checkDuplicateUserName(userDTO.getUserName())
                    .equals(UserRegistrationStatus.ALREADY_EXIST_USER_NAME)) {
                return new ResponseEntity<>("userName exist", HttpStatus.CONFLICT);
            }
        }

        if (userDTO.getPhoneNumber() != null) {
            if (!Pattern.matches("^\\d{2,3}-\\d{3,4}-\\d{4}$", userDTO.getPhoneNumber())) {
                return new ResponseEntity<>("phone number format error", HttpStatus.BAD_REQUEST);
            }
        }

        return ResponseEntity.ok().build();
    }

    /**
     * 입력된 회원들의 정보를 목록으로 조회합니다.
     * 
     * @param page     페이지 번호
     * @param pageSize 한 페이지에 표시될 수 있는 최대 회원 수
     * @param sort     정렬 방식 (가입일순 또는 이름순)
     * @return 회원 목록 정보
     */
    @Operation(summary = "회원 목록 조회", description = "회원 목록 조회 메서드입니다")
    @GetMapping("/list")
    public ResponseEntity<?> getUserList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String createDateSort,
            @RequestParam(required = false) String userNameSort) {
        try {
            Sort sort = null;
            if (createDateSort != null && userNameSort != null) {
                sort = Sort.by(
                        Sort.Order.desc("createDate"),
                        Sort.Order.asc("userName"));
            } else if (createDateSort != null) {
                sort = Sort.by(Sort.Order.desc("createDate"));
            } else if (userNameSort != null) {
                sort = Sort.by(Sort.Order.asc("userName"));
            } else {
                sort = Sort.by(Sort.Order.desc("createDate"));
            }

            PageRequest pageable = PageRequest.of(page, pageSize, sort);
            Page<UserListDTO> userList = userService.getUserList(pageable);
            return ResponseEntity.ok(userList);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>("SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
