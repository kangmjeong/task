package aladdinsys.api.task.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserDTO {

    @NotBlank(message = "아이디를 입력해주세요.")
    public String userId;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    public String password;

    @NotBlank(message = "이름을 입력해주세요.")
    public String name;

    @NotBlank(message = "주민등록번호를 입력해주세요.")
    public String regNo;
}
