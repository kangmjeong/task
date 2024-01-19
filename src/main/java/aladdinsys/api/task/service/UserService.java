package aladdinsys.api.task.service;

import aladdinsys.api.task.dto.AllowedUserDTO;
import aladdinsys.api.task.dto.UserDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface UserService {
    String signUp (UserDTO userDTO);
    String addAllowedUser(AllowedUserDTO allowedUserDTO);

}