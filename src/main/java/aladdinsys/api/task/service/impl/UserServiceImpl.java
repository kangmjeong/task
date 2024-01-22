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
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AllowedUserRepository allowedUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AesBytesEncryptor aesBytesEncryptor;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

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

    public String login(HttpServletRequest req, HttpServletResponse res, UserDTO userDTO) {
        String id = userDTO.getUserId();
        String password = userDTO.getPassword();
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

        if (userDTO.getUserId() != null && !userDTO.getUserId().equals(userId)) {
            throw new IllegalArgumentException("userId를 변경할 수 없습니다.");
        }

        if (userDTO.getRegNo() != null) {
            throw new IllegalArgumentException("RegNO를 변경할 수 없습니다.");
        }

        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            String encodedPassword = passwordEncoder.encode(userDTO.getPassword());
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
        String encryptedRegNo = encryptRegNo(allowedUserDTO.getRegNo());
        AllowedUserEntity user = new AllowedUserEntity(allowedUserDTO.getName(), encryptedRegNo);
        allowedUserRepository.save(user);
        return encryptedRegNo;
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
        UserDTO userDto = new UserDTO();
        userDto.setUserId(userEntity.getUserId());
        userDto.setName(userEntity.getName());
        return userDto;
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
