package aladdinsys.api.task.service.impl;

import aladdinsys.api.task.dto.AllowedUserDTO;
import aladdinsys.api.task.dto.UserDTO;
import aladdinsys.api.task.entity.AllowedUserEntity;
import aladdinsys.api.task.entity.UserEntity;
import aladdinsys.api.task.repository.AllowedUserRepository;
import aladdinsys.api.task.repository.UserRepository;
import aladdinsys.api.task.service.UserService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.encrypt.AesBytesEncryptor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AllowedUserRepository allowedUserRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AesBytesEncryptor aesBytesEncryptor;

    public String signUp(UserDTO userDTO) {
        JsonObject result = new JsonObject();

        // 주민등록번호 암호화
        String encryptedRegNo = encryptRegNo(userDTO.getRegNo());

        // 회원가입 가능 여부 확인 (암호화된 regNo 사용)
        boolean isAllowed = allowedUserRepository
                .findByNameAndRegNo(userDTO.getName(), encryptedRegNo)
                .isPresent();

        if (!isAllowed) {
            return "회원 가입이 불가능한 사용자입니다.";
        }

        // 패스워드 인코딩 - 단방향 암호화 방식 사용(복호화 불가)
        String encodedPassword = passwordEncoder.encode(userDTO.getPassword());

        // 주민등록번호 암호화 로직은 이미 위에서 수행했으므로 중복 제거
        UserEntity user = new UserEntity(
                userDTO.getUserId(),
                encodedPassword,
                userDTO.getName(),
                encryptedRegNo // 이미 암호화된 regNo 사용
        );

        if (!isDuplicated(user.getUserId())) {
            userRepository.save(user);
            result.addProperty("message", "저장이 완료되었습니다.");
            result.addProperty("success", true);
        } else {
            result.addProperty("message", "아이디가 중복되었습니다.");
            result.addProperty("success", false);
        }

        return new Gson().toJson(result);
    }

    /**
     * 아이디 중복 체크
     *
     * @param userId
     * @return
     */
    public boolean isDuplicated(String userId) {
        return userRepository.findByUserId(userId).isPresent();
    }

    /**
     * 회원가입이 가능한 유저 등록
     * @param allowedUserDTO
     * @return
     */
    public String addAllowedUser(AllowedUserDTO allowedUserDTO) {
        String encryptedRegNo = encryptRegNo(allowedUserDTO.getRegNo());
        AllowedUserEntity user = new AllowedUserEntity(allowedUserDTO.getName(), encryptedRegNo);
        allowedUserRepository.save(user);
        return encryptedRegNo;
    }

    /**
     * 암호화 로직
     * @param regNo
     * @return
     */
    private String encryptRegNo(String regNo) {
        byte[] regNoBytes = regNo.getBytes(StandardCharsets.UTF_8);
        byte[] encryptedBytes = aesBytesEncryptor.encrypt(regNoBytes);
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }


}
