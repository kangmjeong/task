package aladdinsys.api.task.controller;


import aladdinsys.api.task.dto.AllowedUserDTO;
import aladdinsys.api.task.dto.UserDTO;
import aladdinsys.api.task.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/szs")
@Slf4j
public class SzsController {

    @Autowired
    private UserService userService;

    /**
     * 회원가입
     * @param userDTO
     * @return
     */
    @RequestMapping(value= "/signup", method = RequestMethod.POST, produces = "text/plain;charset=UTF-8")
    public ResponseEntity<?> signUp(@Valid @RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userService.signUp(userDTO));
    }

    /**
     * 가입 가능한 유저 등록
     * @param allowedUserDto
     * @return
     */
    @RequestMapping(value="/addAllowedUser", method = RequestMethod.POST, produces = "text/plain;charset=UTF-8")
    public ResponseEntity<String> addAllowedUser(@RequestBody AllowedUserDTO allowedUserDto) {
        userService.addAllowedUser(allowedUserDto);
        return ResponseEntity.ok("Allowed user added");
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST, produces = "text/plain;charset=UTF-8")
    public ResponseEntity<?> login(HttpServletRequest req, HttpServletResponse res, @RequestBody UserDTO userDTO) throws Exception {
        return ResponseEntity.ok(userService.login(req, res, userDTO));
    }

}
