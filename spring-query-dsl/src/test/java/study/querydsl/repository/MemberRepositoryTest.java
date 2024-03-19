package study.querydsl.repository;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.EntityManager;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.dto.MemberSearchRequestDto;
import study.querydsl.dto.MemberTeamDto;
import study.querydsl.entity.Member;
import study.querydsl.entity.Team;

@SpringBootTest
@Transactional
class MemberRepositoryTest {

    @Autowired
    EntityManager em;
    @Autowired
    MemberJpaRepository memberJpaRepository;
    @Autowired
    MemberQueryDslRepository memberQueryDslRepository;
    @Autowired
    MemberRepository memberRepository;

    @Test
    @DisplayName("JPA로 회원 저장 및 조회")
    public void basicJpaTest() {
        Member member = new Member("member1", 10);
        memberJpaRepository.save(member);

        Member findMember = memberJpaRepository.findById(member.getId()).get();
        assertThat(findMember).isEqualTo(member);

        List<Member> result1 = memberJpaRepository.findAll();
        assertThat(result1).containsExactly(member);

        List<Member> result2 = memberJpaRepository.findByUsername("member1");
        assertThat(result2).containsExactly(member);
    }

    @Test
    @DisplayName("QueryDsl로 회원 저장 및 조회")
    public void basicQueryDslTest() {
        Member member = new Member("member1", 10);
        memberJpaRepository.save(member);

        List<Member> result1 = memberQueryDslRepository.findAll_QueryDsl();
        assertThat(result1).containsExactly(member);

        List<Member> result2 = memberQueryDslRepository.findByUsername_QueryDsl("member1");
        assertThat(result2).containsExactly(member);
    }

    @Test
    @DisplayName("BooleanBuilder 동적 쿼리 조회")
    void searchByBooleanBuilder() {
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        MemberSearchRequestDto request = new MemberSearchRequestDto();
        request.setTeamName("teamA");
        request.setAgeGoe(10);
        request.setAgeLoe(30);

        List<MemberTeamDto> result = memberQueryDslRepository.searchByBuilder(request);
        assertThat(result).extracting("username").containsExactly("member1", "member2");
    }

    @Test
    @DisplayName("WhereParam 동적 쿼리 조회")
    void searchByWhereParam() {

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        MemberSearchRequestDto request = new MemberSearchRequestDto();
        request.setTeamName("teamA");
        request.setAgeGoe(10);
        request.setAgeLoe(30);

        List<MemberTeamDto> result = memberQueryDslRepository.searchByWhereParam(request);
        assertThat(result).extracting("username").containsExactly("member1", "member2");
    }

    @Test
    @DisplayName("Custom Repository Test")
    void customRepository() {

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        MemberSearchRequestDto request = new MemberSearchRequestDto();
        request.setTeamName("teamA");
        request.setAgeGoe(10);
        request.setAgeLoe(30);

        List<MemberTeamDto> result = memberRepository.search(request);
        assertThat(result).extracting("username").containsExactly("member1", "member2");
    }
}