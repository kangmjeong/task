/* (C) 2024 AladdinSystem License */
package aladdinsys.api.task.controller;


import aladdinsys.api.task.dto.UserDTO;
import aladdinsys.api.task.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping(value = "/szs")
@RequiredArgsConstructor
public class SzsController {

    private final UserService userService;

    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public String signUp(@Valid @RequestBody UserDTO userDTO) throws Exception {
        return userService.signUp(userDTO);
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(@RequestBody UserDTO userDTO) {
        return userService.login(userDTO);
    }

    @RequestMapping(value = "/user-detail", method = RequestMethod.GET)
    public String userDetail(Principal principal) {
        return userService.userDetail(principal.getName());
    }

    @RequestMapping(value = "/modify-user", method = RequestMethod.PUT)
    public String updateUserDetails(@RequestBody UserDTO userDTO, Principal principal) {
        return userService.updateUserDetails(principal.getName(), userDTO);
    }

    @RequestMapping(value = "/delete-user", method = RequestMethod.DELETE)
    public String deleteUser(Principal principal) {
        return userService.deleteUser(principal.getName());
    }
}
