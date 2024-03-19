package study.querydsl.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class MemberSearchRequestDto {

    private String username;
    private String teamName;
    private Integer ageGoe;
    private Integer ageLoe;
}
