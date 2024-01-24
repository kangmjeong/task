package aladdinsys.api.task.utils;

import aladdinsys.api.task.dto.AllowedUserDTO;
import aladdinsys.api.task.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor

public class Init implements CommandLineRunner {

    private final UserService userService;

    @Override
    public void run(String... args) throws Exception {
        List<AllowedUserDTO> allowedUsers = Arrays.asList(
                new AllowedUserDTO("홍길동", "860824-1655068"),
                new AllowedUserDTO("김둘리", "921108-1582816"),
                new AllowedUserDTO("마징가", "880601-2455116"),
                new AllowedUserDTO("베지터", "910411-1656116"),
                new AllowedUserDTO("손오공", "820326-2715702")
        );

        for (AllowedUserDTO user : allowedUsers) {
            userService.addAllowedUsers(user);
        }
    }

}
