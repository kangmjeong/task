package aladdinsys.api.task.controller;


import aladdinsys.api.task.dto.AllowedUserDTO;
import aladdinsys.api.task.dto.UserDTO;
import aladdinsys.api.task.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping(value = "/szs")
public class SzsController {

    private final UserService userService;

    public SzsController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public ResponseEntity<?> signUp(@RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userService.signUp(userDTO));
    }

    @RequestMapping(value = "/allowed-user", method = RequestMethod.POST)
    public ResponseEntity<String> addAllowedUser(@RequestBody AllowedUserDTO allowedUserDto) {
        return ResponseEntity.ok(userService.addAllowedUser(allowedUserDto));
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<?> login(HttpServletRequest req, HttpServletResponse res, @RequestBody UserDTO userDTO) throws Exception {
        return ResponseEntity.ok(userService.login(req, res, userDTO));
    }

    @RequestMapping(value = "/me", method = RequestMethod.GET)
    public ResponseEntity<?> me(HttpServletRequest req) {
        return ResponseEntity.ok(userService.me(req));
    }

    @RequestMapping(value = "/modify", method = RequestMethod.PUT)
    public ResponseEntity<?> modify(@RequestBody UserDTO userDTO, HttpServletRequest req, Principal principal) {
        String userId = principal.getName();
        return ResponseEntity.ok(userService.updateUserDetails(userId, userDTO, req));
    }

    @RequestMapping(value = "/delete/{userId}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteUser(@PathVariable String userId, HttpServletRequest req) {
        try {
            return ResponseEntity.ok(userService.deleteUser(userId, req));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

}
