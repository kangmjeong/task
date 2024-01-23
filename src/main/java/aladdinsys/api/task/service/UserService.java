package aladdinsys.api.task.service;

import aladdinsys.api.task.dto.AllowedUserDTO;
import aladdinsys.api.task.dto.UserDTO;
import aladdinsys.api.task.entity.AllowedUserEntity;
import aladdinsys.api.task.entity.UserEntity;
import aladdinsys.api.task.repository.AllowedUserRepository;
import aladdinsys.api.task.repository.UserRepository;
import aladdinsys.api.task.utils.jwt.JwtTokenUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.encrypt.AesBytesEncryptor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final AllowedUserRepository allowedUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final AesBytesEncryptor aesBytesEncryptor;
    private final JwtTokenUtil jwtTokenUtil;

    public UserService(UserRepository userRepository, AllowedUserRepository allowedUserRepository, PasswordEncoder passwordEncoder, AesBytesEncryptor aesBytesEncryptor, JwtTokenUtil jwtTokenUtil) {
        this.userRepository = userRepository;
        this.allowedUserRepository = allowedUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.aesBytesEncryptor = aesBytesEncryptor;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    public String signUp(UserDTO userDTO) {
        JsonObject result = new JsonObject();

        String encryptedRegNo = encryptRegNo(userDTO.regNo());

        boolean isAllowed = allowedUserRepository
                .findByNameAndRegNo(userDTO.name(), encryptedRegNo)
                .isPresent();

        if (!isAllowed) {
            return "회원 가입이 불가능한 사용자입니다.";
        }

        String encodedPassword = passwordEncoder.encode(userDTO.password());

        UserEntity user = new UserEntity(
                userDTO.userId(),
                encodedPassword,
                userDTO.name(),
                encryptedRegNo
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

    public String login(UserDTO userDTO) {
        String id = userDTO.userId();
        String password = userDTO.password();
        try {
            UserEntity userEntity = userRepository.findByUserId(id)
                    .orElseThrow(() -> new UsernameNotFoundException("회원이 존재하지 않습니다."));

            if (!passwordEncoder.matches(password, userEntity.getPassword())) {
                throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
            }

            UserDTO userDto = convertEntityToDto(userEntity);
            String token = jwtTokenUtil.generateToken(userDto);
            return token;

        } catch (UsernameNotFoundException e) {
            return "아이디가 틀렸습니다.";
        } catch (BadCredentialsException e) {
            return "비밀번호가 틀렸습니다.";
        }
    }

    public String userDetail() throws UsernameNotFoundException, BadCredentialsException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
        return new Gson().toJson(userEntity);
    }

    public String updateUserDetails(String userId, UserDTO userDTO) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String authenticatedUserId = authentication.getName();

            if (!authenticatedUserId.equals(userId)) {
                return "수정할 수 있는 권한이 없습니다.";
            }

            if (userDTO.userId() != null && !userDTO.userId().equals(userId)) {
                return "사용자 ID는 변경할 수 없습니다.";
            }

            if (userDTO.regNo() != null) {
                return "주민등록번호는 변경할 수 없습니다.";
            }

            UserEntity userEntity = userRepository.findById(userId)
                    .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

            if (userDTO.password() != null && !userDTO.password().isEmpty()) {
                String encodedPassword = passwordEncoder.encode(userDTO.password());
                userEntity.setPassword(encodedPassword);
            }

            userRepository.save(userEntity);

            return "사용자 정보가 성공적으로 업데이트되었습니다.";

        } catch (Exception ex) {
            return "사용자 정보 업데이트 중 오류가 발생했습니다.";
        }
    }

    public String deleteUser(String userId) {
        try {
            UserEntity userEntity = userRepository.findById(userId)
                    .orElseThrow(() -> new UsernameNotFoundException("삭제할 사용자를 찾을 수 없습니다."));

            userRepository.delete(userEntity);
            return "사용자가 성공적으로 삭제되었습니다.";
        } catch (Exception ex) {
            return "사용자 삭제 중 예기치 못한 오류가 발생했습니다.";
        }
    }

    public String addAllowedUsers(AllowedUserDTO allowedUserDTO) {
        String encryptedRegNo = encryptRegNo(allowedUserDTO.regNo());
        AllowedUserEntity user = new AllowedUserEntity(allowedUserDTO.name(), encryptedRegNo);
        allowedUserRepository.save(user);
        return "저장이 완료되었습니다.";
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

    private String encryptRegNo(String regNo) {
        byte[] regNoBytes = regNo.getBytes(StandardCharsets.UTF_8);
        byte[] encryptedBytes = aesBytesEncryptor.encrypt(regNoBytes);
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    private UserDTO convertEntityToDto(UserEntity userEntity) {
        return new UserDTO(
                userEntity.getUserId(),
                userEntity.getPassword(),
                userEntity.getName(),
                userEntity.getRegNo()
        );
    }

}
