/* (C) 2024 AladdinSystem License */
package aladdinsys.api.task.controller;


import aladdinsys.api.task.dto.UserDTO;
import aladdinsys.api.task.service.UserService;
import jakarta.validation.Valid;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/szs")
@RequiredArgsConstructor
public class SzsController {

  private final UserService userService;

  @RequestMapping(
      value = "/signup",
      method = RequestMethod.POST,
      consumes = "application/json",
      produces = "application/json")
  public String signUp(@Valid @RequestBody UserDTO userDTO) {
    return userService.signUp(userDTO);
  }

  @RequestMapping(
      value = "/login",
      method = RequestMethod.POST,
      consumes = "application/json",
      produces = "application/json")
  public String login(@RequestBody UserDTO userDTO) {
    return userService.login(userDTO);
  }

  @RequestMapping(value = "/user-detail", method = RequestMethod.GET, produces = "application/json")
  public String userDetail(Principal principal) {
    return userService.userDetail(principal.getName());
  }

  @RequestMapping(
      value = "/modify-user",
      method = RequestMethod.PUT,
      consumes = "application/json",
      produces = "application/json")
  public String updateUserDetails(@RequestBody UserDTO userDTO, Principal principal) {
    return userService.updateUserDetails(principal.getName(), userDTO);
  }

  @RequestMapping(
      value = "/delete-user",
      method = RequestMethod.DELETE,
      consumes = "application/json",
      produces = "application/json")
  public String deleteUser(Principal principal) {
    return userService.deleteUser(principal.getName());
  }
}
