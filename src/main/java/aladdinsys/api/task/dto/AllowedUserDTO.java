package aladdinsys.api.task.dto;

import jakarta.validation.constraints.NotBlank;

public record AllowedUserDTO(@NotBlank(message = "아이디를 입력해주세요.")
                             String name,

                             @NotBlank(message = "주민등록번호를 입력해주세요.")
                             String regNo) {

}

