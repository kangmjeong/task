package aladdinsys.api.task.service;

import aladdinsys.api.task.dto.AllowedUserDTO;
import aladdinsys.api.task.dto.UserDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface UserService {
    String signUp(UserDTO userDTO);

    String addAllowedUser(AllowedUserDTO allowedUserDTO);

    String login(HttpServletRequest req, HttpServletResponse res, UserDTO userDTO);

    String me(HttpServletRequest req);

    boolean updateUserDetails(String userId, UserDTO userDTO, HttpServletRequest req);

    boolean deleteUser(String userId, HttpServletRequest req);


}
