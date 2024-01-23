package aladdinsys.api.task.service;

import aladdinsys.api.task.dto.AllowedUserDTO;
import aladdinsys.api.task.dto.UserDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;


public interface UserService {
    String signUp(UserDTO userDTO);

    String addAllowedUser(AllowedUserDTO allowedUserDTO);

    String login(HttpServletRequest req, HttpServletResponse res, UserDTO userDTO);

    String me(HttpServletRequest req);

    String updateUserDetails(String userId, UserDTO userDTO, HttpServletRequest req);

    String deleteUser(String userId, HttpServletRequest req);


}
