package study.querydsl.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import study.querydsl.dto.MemberSearchRequestDto;
import study.querydsl.dto.MemberTeamDto;
import study.querydsl.repository.MemberQueryDslRepository;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberQueryDslRepository repository;

    @GetMapping("/v1/members")
    public List<MemberTeamDto> searchMemberV1(MemberSearchRequestDto request) {
        return repository.searchByWhereParam(request);
    }

}
