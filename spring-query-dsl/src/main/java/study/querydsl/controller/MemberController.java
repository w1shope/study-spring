package study.querydsl.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import study.querydsl.dto.MemberSearchRequestDto;
import study.querydsl.dto.MemberTeamDto;
import study.querydsl.repository.MemberQueryDslRepository;
import study.querydsl.repository.MemberRepository;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberQueryDslRepository repository;
    private final MemberRepository memberRepository;

    @GetMapping("/v1/members")
    public List<MemberTeamDto> searchMemberV1(MemberSearchRequestDto request) {
        return repository.searchByWhereParam(request);
    }

    @GetMapping("/v2/members")
    public Page<MemberTeamDto> searchMemberV2_Simple(MemberSearchRequestDto request, Pageable pageable) {
        return memberRepository.searchPageSimple(request, pageable);
    }

    @GetMapping("/v3/members")
    public Page<MemberTeamDto> searchMemberV2_Complex(MemberSearchRequestDto request, Pageable pageable) {
        return memberRepository.searchPageComplex(request, pageable);
    }

}
