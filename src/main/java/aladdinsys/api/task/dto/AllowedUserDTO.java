package aladdinsys.api.task.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AllowedUserDTO {

    @NotBlank(message = "아이디를 입력해주세요.")
    public String name;

    @NotBlank(message = "주민등록번호를 입력해주세요.")
    public String regNo;

}

