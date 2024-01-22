package aladdinsys.api.task.controller;


import aladdinsys.api.task.dto.AllowedUserDTO;
import aladdinsys.api.task.dto.UserDTO;
import aladdinsys.api.task.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/szs")
@Slf4j
public class SzsController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/signup", method = RequestMethod.POST, produces = "text/plain;charset=UTF-8")
    public ResponseEntity<?> signUp(@Valid @RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userService.signUp(userDTO));
    }

    @RequestMapping(value = "/addAllowedUser", method = RequestMethod.POST, produces = "text/plain;charset=UTF-8")
    public ResponseEntity<String> addAllowedUser(@RequestBody AllowedUserDTO allowedUserDto) {
        userService.addAllowedUser(allowedUserDto);
        return ResponseEntity.ok("Allowed user added");
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST, produces = "text/plain;charset=UTF-8")
    public ResponseEntity<?> login(HttpServletRequest req, HttpServletResponse res, @RequestBody UserDTO userDTO) throws Exception {
        return ResponseEntity.ok(userService.login(req, res, userDTO));
    }

    @RequestMapping(value = "/me", method = RequestMethod.GET, produces = "text/plain;charset=UTF-8")
    public ResponseEntity<?> me(HttpServletRequest req) {
        return ResponseEntity.ok(userService.me(req));
    }

    @RequestMapping(value = "/modify/{userId}", method = RequestMethod.PUT, produces = "text/plain;charset=UTF-8")
    public ResponseEntity<?> modify(@PathVariable String userId, @RequestBody UserDTO userDTO, HttpServletRequest req) {
        userService.updateUserDetails(userId, userDTO, req);
        return ResponseEntity.ok("회원 정보가 업데이트되었습니다.");
    }

    @RequestMapping(value = "/delete/{userId}", method = RequestMethod.DELETE, produces = "text/plain;charset=UTF-8")
    public ResponseEntity<?> deleteUser(@PathVariable String userId, HttpServletRequest req) {
        try {
            userService.deleteUser(userId, req);
            return ResponseEntity.ok("사용자가 성공적으로 삭제되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage()); //권한이 없는 경우
        }
    }


}
