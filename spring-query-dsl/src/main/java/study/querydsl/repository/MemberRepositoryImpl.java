package study.querydsl.repository;

import static study.querydsl.entity.QMember.*;
import static study.querydsl.entity.QTeam.*;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import study.querydsl.dto.MemberSearchRequestDto;
import study.querydsl.dto.MemberTeamDto;
import study.querydsl.dto.QMemberTeamDto;
import study.querydsl.entity.Member;

public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public MemberRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<MemberTeamDto> search(MemberSearchRequestDto request) {
        return queryFactory.select(
                new QMemberTeamDto(member.id, member.username, member.age, team.id, team.name))
            .from(member).leftJoin(member.team, team)
            .where(usernameEq(request.getUsername()), teamNameEq(request.getTeamName()),
                ageGoe(request.getAgeGoe()), ageLoe(request.getAgeLoe())).fetch();
    }

    @Override
    public Page<MemberTeamDto> searchPageSimple(MemberSearchRequestDto request, Pageable pageable) {
        QueryResults<MemberTeamDto> result = queryFactory.select(
                new QMemberTeamDto(member.id, member.username, member.age, team.id, team.name))
            .from(member).leftJoin(member.team, team)
            .where(usernameEq(request.getUsername()), teamNameEq(request.getTeamName()),
                ageGoe(request.getAgeGoe()), ageLoe(request.getAgeLoe()))
            .offset(pageable.getOffset()).limit(pageable.getPageSize()).fetchResults();

        List<MemberTeamDto> content = result.getResults();
        long total = result.getTotal();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<MemberTeamDto> searchPageComplex(MemberSearchRequestDto request,
        Pageable pageable) {
        List<MemberTeamDto> result = queryFactory.select(new QMemberTeamDto(
                member.id, member.username, member.age, team.id, team.name
            )).from(member)
            .leftJoin(member.team, team)
            .where(usernameEq(request.getUsername()), teamNameEq(request.getTeamName()),
                ageGoe(request.getAgeGoe()), ageLoe(request.getAgeLoe()))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        // 생성 시점에는 count 쿼리 발생 x
        JPAQuery<Member> queryCount = queryFactory.selectFrom(member)
            .leftJoin(member.team, team)
            .where(usernameEq(request.getUsername()), teamNameEq(request.getTeamName()),
                ageGoe(request.getAgeGoe()), ageLoe(request.getAgeLoe()));

        // queryCount::fetchCount -> count 쿼리 발생
        return PageableExecutionUtils.getPage(result, pageable, queryCount::fetchCount);
//        return new PageImpl<>(result, pageable, queryCount.fetchCount());
    }

    private BooleanExpression usernameEq(String username) {
        return username != null ? member.username.eq(username) : null;
    }

    private BooleanExpression teamNameEq(String teamName) {
        return teamName != null ? team.name.eq(teamName) : null;
    }

    private BooleanExpression ageGoe(Integer age) {
        return age != null ? member.age.goe(age) : null;
    }

    private BooleanExpression ageLoe(Integer age) {
        return age != null ? member.age.loe(age) : null;
    }
}
