package study.querydsl.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import study.querydsl.dto.MemberSearchRequestDto;
import study.querydsl.dto.MemberTeamDto;

public interface MemberRepositoryCustom {

    List<MemberTeamDto> search(MemberSearchRequestDto requestDto);
    Page<MemberTeamDto> searchPageSimple(MemberSearchRequestDto request, Pageable pageable);
    Page<MemberTeamDto> searchPageComplex(MemberSearchRequestDto request, Pageable pageable);
}
