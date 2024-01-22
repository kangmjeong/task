package aladdinsys.api.task.service.impl;

import aladdinsys.api.task.dto.AllowedUserDTO;
import aladdinsys.api.task.dto.UserDTO;
import aladdinsys.api.task.entity.AllowedUserEntity;
import aladdinsys.api.task.entity.UserEntity;
import aladdinsys.api.task.repository.AllowedUserRepository;
import aladdinsys.api.task.repository.UserRepository;
import aladdinsys.api.task.service.UserService;
import aladdinsys.api.task.utils.jwt.JwtTokenUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.encrypt.AesBytesEncryptor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AllowedUserRepository allowedUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final AesBytesEncryptor aesBytesEncryptor;
    private final JwtTokenUtil jwtTokenUtil;

    public UserServiceImpl(UserRepository userRepository, AllowedUserRepository allowedUserRepository, PasswordEncoder passwordEncoder, AesBytesEncryptor aesBytesEncryptor, JwtTokenUtil jwtTokenUtil) {
        this.userRepository = userRepository;
        this.allowedUserRepository = allowedUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.aesBytesEncryptor = aesBytesEncryptor;
        this.jwtTokenUtil = jwtTokenUtil;
    }


    public String signUp(UserDTO userDTO) {
        JsonObject result = new JsonObject();

        // 주민등록번호 암호화
        String encryptedRegNo = encryptRegNo(userDTO.regNo());

        // 회원가입 가능 여부 확인 (암호화된 regNo 사용)
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

    public String login(HttpServletRequest req, HttpServletResponse res, UserDTO userDTO) {
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

    @Override
    public String me(HttpServletRequest req) throws UsernameNotFoundException, BadCredentialsException {
        String jwt = jwtTokenUtil.getJwtFromRequest(req);
        if (jwt != null && jwtTokenUtil.validateToken(jwt)) {
            String userId = jwtTokenUtil.getUserIdFromToken(jwt);
            UserEntity userEntity = userRepository.findById(userId)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found."));
            return new Gson().toJson(userEntity);
        } else {
            throw new BadCredentialsException("Invalid token.");
        }
    }

    @Override
    public boolean updateUserDetails(String userId, UserDTO userDTO, HttpServletRequest req) {
        String authenticatedUserId = verifyTokenAndGetUserId(req);
        if (!authenticatedUserId.equals(userId)) {
            throw new BadCredentialsException("사용자 ID가 일치하지 않습니다.");
        }

        if (userDTO.userId() != null && !userDTO.userId().equals(userId)) {
            throw new IllegalArgumentException("userId를 변경할 수 없습니다.");
        }

        if (userDTO.regNo() != null) {
            throw new IllegalArgumentException("RegNO를 변경할 수 없습니다.");
        }

        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        if (userDTO.password() != null && !userDTO.password().isEmpty()) {
            String encodedPassword = passwordEncoder.encode(userDTO.password());
            userEntity.setPassword(encodedPassword);
        }
        userRepository.save(userEntity);
        return true;
    }

    @Override
    public boolean deleteUser(String userId, HttpServletRequest req) {
        String authenticatedUserId = verifyTokenAndGetUserId(req);
        if (!authenticatedUserId.equals(userId)) {
            throw new BadCredentialsException("삭제 권한이 없습니다.");
        }

        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("삭제할 사용자를 찾을 수 없습니다."));

        userRepository.delete(userEntity);
        return true;
    }

    /**
     * 회원가입이 가능한 유저 등록
     *
     * @param allowedUserDTO
     * @return
     */
    public String addAllowedUser(AllowedUserDTO allowedUserDTO) {
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

    /**
     * 암호화 로직
     *
     * @param regNo
     * @return
     */
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

    /**
     * 토큰 검증 및 인증된 사용자 ID 반환
     *
     * @param req HttpServletRequest
     * @return 인증된 사용자 ID
     * @throws BadCredentialsException 토큰이 유효하지 않은 경우
     */
    private String verifyTokenAndGetUserId(HttpServletRequest req) throws BadCredentialsException {
        String jwt = jwtTokenUtil.getJwtFromRequest(req);
        if (jwt != null && jwtTokenUtil.validateToken(jwt)) {
            return jwtTokenUtil.getUserIdFromToken(jwt);
        } else {
            throw new BadCredentialsException("유효하지 않은 토큰입니다.");
        }
    }


}
