package aladdinsys.api.task.controller;


import aladdinsys.api.task.dto.AllowedUserDTO;
import aladdinsys.api.task.dto.UserDTO;
import aladdinsys.api.task.service.UserService;
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

    @RequestMapping(value = "/allowed-users", method = RequestMethod.POST)
    public ResponseEntity<String> addAllowedUsers(@RequestBody AllowedUserDTO allowedUserDto) {
        return ResponseEntity.ok(userService.addAllowedUsers(allowedUserDto));
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<?> login(@RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userService.login(userDTO));
    }

    @RequestMapping(value = "/user-detail", method = RequestMethod.GET)
    public ResponseEntity<?> userDetail() {
        String result = userService.userDetail();
        return ResponseEntity.ok(result);
    }

    @RequestMapping(value = "/user", method = RequestMethod.PUT)
    public ResponseEntity<?> updateUserDetails(@RequestBody UserDTO userDTO, Principal principal) {
        String userId = principal.getName();
        return ResponseEntity.ok(userService.updateUserDetails(userId, userDTO));
    }

    @RequestMapping(value = "/user/{userId}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteUser(@PathVariable String userId) {
        try {
            return ResponseEntity.ok(userService.deleteUser(userId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

}
