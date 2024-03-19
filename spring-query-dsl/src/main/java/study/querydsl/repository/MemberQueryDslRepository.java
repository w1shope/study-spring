package study.querydsl.repository;

import static org.springframework.util.StringUtils.hasText;
import static study.querydsl.entity.QMember.member;
import static study.querydsl.entity.QTeam.team;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.springframework.stereotype.Repository;
import study.querydsl.dto.MemberSearchRequestDto;
import study.querydsl.dto.MemberTeamDto;
import study.querydsl.dto.QMemberTeamDto;
import study.querydsl.entity.Member;

@Repository
public class MemberQueryDslRepository {

    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    public MemberQueryDslRepository(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    public List<Member> findByUsername_QueryDsl(String username) {
        return queryFactory.selectFrom(member)
            .where(member.username.eq(username))
            .fetch();
    }

    public List<Member> findAll_QueryDsl() {
        return queryFactory.selectFrom(member)
            .fetch();
    }

    public List<MemberTeamDto> searchByBuilder(MemberSearchRequestDto request) {
        BooleanBuilder builder = new BooleanBuilder();
        if (hasText(request.getUsername())) {
            builder.and(member.username.eq(request.getUsername()));
        }
        if (hasText(request.getTeamName())) {
            builder.and(team.name.eq(request.getTeamName()));
        }
        if (request.getAgeGoe() != null) {
            builder.and(member.age.goe(request.getAgeGoe()));
        }
        if (request.getAgeLoe() != null) {
            builder.and(member.age.loe(request.getAgeLoe()));
        }

        return queryFactory.select(new QMemberTeamDto(
                member.id, member.username, member.age, team.id, team.name))
            .from(member)
            .leftJoin(member.team, team)
            .where(builder)
            .fetch();
    }

    public List<MemberTeamDto> searchByWhereParam(MemberSearchRequestDto request) {
        return queryFactory.select(new QMemberTeamDto(
                member.id, member.username, member.age, team.id, team.name
            ))
            .from(member)
            .where(usernameEq(request.getUsername()), teamNameEq(request.getTeamName()),
                ageGoe(request.getAgeGoe()), ageLoe(request.getAgeLoe()))
            .fetch();
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
