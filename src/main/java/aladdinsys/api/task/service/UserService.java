/* (C) 2024 AladdinSystem License */
package aladdinsys.api.task.service;


import aladdinsys.api.task.Exception.CustomServiceException;
import aladdinsys.api.task.dto.AllowedUserDTO;
import aladdinsys.api.task.dto.UserDTO;
import aladdinsys.api.task.entity.AllowedUserEntity;
import aladdinsys.api.task.entity.UserEntity;
import aladdinsys.api.task.repository.AllowedUserRepository;
import aladdinsys.api.task.repository.UserRepository;
import aladdinsys.api.task.utils.jwt.JwtTokenUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.encrypt.AesBytesEncryptor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AllowedUserRepository allowedUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final AesBytesEncryptor aesBytesEncryptor;
    private final JwtTokenUtil jwtTokenUtil;

    public String signUp(UserDTO userDTO) {

        String encryptedRegNo = encryptRegNo(userDTO.regNo());

        boolean isAllowed = allowedUserRepository.findByNameAndRegNo(userDTO.name(), encryptedRegNo).isPresent();
        if (!isAllowed) {
            throw new CustomServiceException(HttpStatus.BAD_REQUEST, "회원 가입이 불가능한 사용자입니다.");
        }

        String encodedPassword = passwordEncoder.encode(userDTO.password());

        UserEntity user =
                new UserEntity(userDTO.userId(), encodedPassword, userDTO.name(), encryptedRegNo);

        if (isDuplicated(user.getUserId())) {
            throw new CustomServiceException(HttpStatus.BAD_REQUEST, "아이디가 중복되었습니다.");
        }

        userRepository.save(user);

        JsonObject jsonResponse = new JsonObject();
        jsonResponse.addProperty("success", true);
        jsonResponse.addProperty("message", "저장이 완료되었습니다.");

        return jsonResponse.toString();
    }

    public String login(UserDTO userDTO) {
        UserEntity userEntity = userRepository.findByUserId(userDTO.userId())
                .orElseThrow(() -> new CustomServiceException(HttpStatus.UNAUTHORIZED, "회원이 존재하지 않습니다."));

        if (!passwordEncoder.matches(userDTO.password(), userEntity.getPassword())) {
            throw new CustomServiceException(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다.");
        }

        UserDTO userDtoForToken = new UserDTO(userEntity.getUserId(), null, userEntity.getName(), null);
        String token = jwtTokenUtil.generateToken(userDtoForToken);

        JsonObject jsonResponse = new JsonObject();
        jsonResponse.addProperty("success", true);
        jsonResponse.addProperty("message", "로그인 성공");
        jsonResponse.addProperty("token", token);
        return jsonResponse.toString();
    }

    public String userDetail(String userId) {

        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new CustomServiceException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));

        UserDTO userDto = new UserDTO(
                userEntity.getUserId(),
                userEntity.getPassword(),
                userEntity.getName(),
                userEntity.getRegNo()
        );

        JsonObject jsonResponse = new JsonObject();
        jsonResponse.addProperty("success", true);
        jsonResponse.add("user", new Gson().toJsonTree(userDto));
        return jsonResponse.toString();
    }

    public String updateUserDetails(String userId, UserDTO userDTO) {
        if (userDTO.userId() != null && !userDTO.userId().equals(userId)) {
            throw new CustomServiceException(HttpStatus.BAD_REQUEST, "사용자 ID는 변경할 수 없습니다.");
        }

        if (userDTO.regNo() != null) {
            throw new CustomServiceException(HttpStatus.BAD_REQUEST, "주민등록번호는 변경할 수 없습니다.");
        }

        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new CustomServiceException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));

        if (userDTO.password() != null && !userDTO.password().isEmpty()) {
            String encodedPassword = passwordEncoder.encode(userDTO.password());
            userEntity.setPassword(encodedPassword);
        }

        userRepository.save(userEntity);

        JsonObject jsonResponse = new JsonObject();
        jsonResponse.addProperty("success", true);
        jsonResponse.addProperty("message", "사용자 정보가 성공적으로 업데이트되었습니다.");
        return jsonResponse.toString();
    }

    public String deleteUser(String userId) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new CustomServiceException(HttpStatus.NOT_FOUND, "삭제할 사용자를 찾을 수 없습니다."));

        userRepository.delete(userEntity);

        JsonObject jsonResponse = new JsonObject();
        jsonResponse.addProperty("success", true);
        jsonResponse.addProperty("message", "사용자가 성공적으로 삭제되었습니다.");
        return jsonResponse.toString();
    }

    public void addAllowedUsers(AllowedUserDTO allowedUserDTO) {
        String encryptedRegNo = encryptRegNo(allowedUserDTO.regNo());
        AllowedUserEntity user = new AllowedUserEntity(allowedUserDTO.name(), encryptedRegNo);
        allowedUserRepository.save(user);
    }

    public boolean isDuplicated(String userId) {
        return userRepository.findByUserId(userId).isPresent();
    }

    private String encryptRegNo(String regNo) {
        byte[] regNoBytes = regNo.getBytes(StandardCharsets.UTF_8);
        byte[] encryptedBytes = aesBytesEncryptor.encrypt(regNoBytes);
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

}
