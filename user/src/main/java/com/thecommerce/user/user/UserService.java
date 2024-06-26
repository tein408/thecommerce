package com.thecommerce.user.user;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thecommerce.user.user.userDTO.UserDTO;
import com.thecommerce.user.user.userDTO.UserListDTO;
import com.thecommerce.user.user.status.UserRegistrationStatus;
import com.thecommerce.user.user.status.UserUpdateStatus;
import com.thecommerce.user.user.userDTO.UpdateUserDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    /**
     * 사용자 정보를 저장하여 회원가입을 처리합니다.
     *
     * @param userDTO 회원 정보
     * @return 회원가입 처리 결과를 나타내는 UserRegistrationStatus 열거형
     *         - OK: 회원가입이 성공한 경우
     *         - FAIL: 회원가입 처리 중 오류가 발생한 경우
     */
    @Transactional(rollbackFor = Exception.class)
    public UserRegistrationStatus save(UserDTO userDTO) {
        try {
            User user = new User();
            user.setUserId(userDTO.getUserId());
            user.setPassword(encoder.encode(userDTO.getPassword()) + "");
            user.setEmail(userDTO.getEmail());
            user.setUserName(userDTO.getUserName());
            user.setPhoneNumber(userDTO.getPhoneNumber());
            user.setCreateDate(LocalDateTime.now());
            userRepository.save(user);
            return UserRegistrationStatus.OK;
        } catch (Exception e) {
            log.error(e.getMessage());
            return UserRegistrationStatus.FAIL;
        }
    }

    /**
     * 회원가입을 위한 이메일 중복 검사를 수행합니다.
     *
     * @param email 검사할 이메일 주소
     * @return 중복 여부에 따른 상태를 나타내는 UserRegistrationStatus 열거형
     *         - ALREADY_EXIST_EMAIL: 이미 등록된 이메일인 경우
     *         - OK: 사용 가능한 이메일인 경우
     *         - FAIL: 데이터베이스 조회 중 오류가 발생한 경우
     */
    @Transactional(rollbackFor = Exception.class)
    public UserRegistrationStatus checkDuplicateEmail(String email) {
        try {
            Optional<User> userOptional = userRepository.findUserByEmail(email);
            return userOptional.isPresent() ? UserRegistrationStatus.ALREADY_EXIST_EMAIL : UserRegistrationStatus.OK;
        } catch (Exception e) {
            log.error(e.getMessage());
            return UserRegistrationStatus.FAIL;
        }
    }

    /**
     * 회원가입 시 사용될 닉네임의 중복 여부를 확인합니다.
     *
     * @param userName 검사할 닉네임
     * @return UserRegistrationStatus 타입의 결과를 반환합니다.
     *         - ALREADY_EXIST_USER_NAME: 이미 존재하는 닉네임인 경우
     *         - OK: 사용 가능한 닉네임인 경우
     * @throws RuntimeException 데이터베이스 조회 중 오류가 발생한 경우
     */
    @Transactional(rollbackFor = Exception.class)
    public UserRegistrationStatus checkDuplicateUserName(String userName) {
        try {
            Optional<User> userOptional = userRepository.findUserByUserName(userName);
            return userOptional.isPresent() ? UserRegistrationStatus.ALREADY_EXIST_USER_NAME
                    : UserRegistrationStatus.OK;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException("SERVER_ERROR");
        }
    }

    /**
     * 회원아이디를 통해 조회한 회원정보를 수정합니다.
     *
     * @param userDTO 회원 정보
     * @param userId  회원 아이디
     * @return 회원 정보 수정 성공 시 UserUpdateStatus.OK, 유효하지 않은 사용자인 경우
     *         UserUpdateStatus.INVALID_USER,
     *         서버 에러 시 UserUpdateStatus.SERVER_ERROR를 반환합니다.
     */
    @Transactional(rollbackFor = Exception.class)
    public UserUpdateStatus updateUser(UpdateUserDTO userDTO, String userId) {
        Optional<User> optionalUser = userRepository.findUserByUserId(userId);
        if (!optionalUser.isPresent()) {
            return UserUpdateStatus.INVALID_USER;
        }

        User user = optionalUser.get();

        if (userDTO.getPassword() != null) {
            user.setPassword(encoder.encode(userDTO.getPassword()));
        }

        if (userDTO.getUserName() != null) {
            user.setUserName(userDTO.getUserName());
        }

        if (userDTO.getPhoneNumber() != null) {
            user.setPhoneNumber(userDTO.getPhoneNumber());
        }

        try {
            userRepository.save(user);
            return UserUpdateStatus.OK;
        } catch (Exception e) {
            log.info(e.getMessage());
            return UserUpdateStatus.SERVER_ERROR;
        }
    }

    /**
     * 입력된 페이지 및 정렬 조건에 따라 회원 목록을 조회합니다.
     * 
     * @param pageable 페이지 및 정렬 정보
     * @return 회원 목록 페이지
     */
    public Page<UserListDTO> getUserList(Pageable pageable) {
        Page<User> result = userRepository.findAll(pageable);
        return result.map(user -> {
            return new UserListDTO(
                    user.getUserIndex(),
                    user.getUserId(),
                    user.getUserName(),
                    user.getEmail(),
                    user.getPhoneNumber(),
                    user.getCreateDate());
        });
    }
}
