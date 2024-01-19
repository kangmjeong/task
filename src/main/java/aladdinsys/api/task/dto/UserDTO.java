package aladdinsys.api.task.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserDTO {

    @NotBlank(message = "아이디를 입력해주세요.")
    @Size(min = 4, max = 12, message = "아이디는 4글자 이상, 12글자 이하로 입력해주세요.")
    public String userId;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Size(min = 8, max = 20, message = "비밀번호는 8글자 이상, 20글자 이하로 입력해주세요.")
    public String password;

    @NotBlank(message = "이름을 입력해주세요.")
    public String name;

    @NotBlank(message = "주민등록번호를 입력해주세요.")
    public String regNo;
}
