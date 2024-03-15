package study.querydsl;

import static org.assertj.core.api.Assertions.assertThat;
import static study.querydsl.entity.QMember.member;
import static study.querydsl.entity.QTeam.team;

import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;
import java.util.List;
import org.aspectj.weaver.ast.Expr;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.entity.Member;
import study.querydsl.entity.Team;

@SpringBootTest
@Transactional
@Commit
public class QueryDslBasicTest {

    @Autowired
    EntityManager em;

    JPAQueryFactory queryFactory;

    @BeforeEach
    void beforeEach() {
        queryFactory = new JPAQueryFactory(em);
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
    }

    @Test
    @DisplayName("JPQL 조회")
    void startJPQL() {
        // member1 조회
        Member findMember = em.createQuery(
                "select m from Member m where m.username = :username",
                Member.class)
            .setParameter("username", "member1")
            .getSingleResult();

        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    @Test
    @DisplayName("QueryDsl 조회")
    void startQueryDsl() {
        Member findMember = queryFactory.select(member)
            .from(member)
            .where(member.username.eq("member1"))
            .fetchOne();

        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    @Test
    @DisplayName("QueryDsl - 다양한 문법")
    void search() {
        Member findMember = queryFactory.selectFrom(member)
            .where(member.username.eq("member1").and(member.age.eq(10)))
            .fetchOne();

        assertThat(findMember.getUsername()).isEqualTo("member1");
        assertThat(findMember.getAge()).isEqualTo(10);

        List<Member> result = queryFactory.selectFrom(member)
            .where(
                member.username.in("member1", "member2")
            ).fetch();
        assertThat(result.size()).isEqualTo(2);

        findMember = queryFactory.selectFrom(member)
            .where(
                member.username.notIn("member1", "member2", "member3")
            ).fetchOne();
        assertThat(findMember.getUsername()).isEqualTo("member4");

        result = queryFactory.selectFrom(member)
            .where(
                member.age.gt(10)
            ).fetch();
        assertThat(result.size()).isEqualTo(3);

        result = queryFactory.selectFrom(member)
            .where(
                member.username.like("member%")
            ).fetch();
        assertThat(result.size()).isEqualTo(4);

        result = queryFactory.selectFrom(member)
            .where(
                member.username.startsWith("member")
            ).fetch();
        assertThat(result.size()).isEqualTo(4);
    }

    @Test
    @DisplayName("QueryDsl - and 문법")
    void searchAndParam() {
        Member findMember1 = queryFactory.selectFrom(member)
            .where(
                member.username.eq("member1"),
                member.age.eq(10)
            ).fetchOne();

        Member findMember2 = queryFactory.selectFrom(member)
            .where(member.username.eq("member1").and(member.age.eq(10)))
            .fetchOne();

        assertThat(findMember1.getUsername()).isEqualTo("member1");
        assertThat(findMember1.getAge()).isEqualTo(10);

        assertThat(findMember2.getUsername()).isEqualTo("member1");
        assertThat(findMember2.getAge()).isEqualTo(10);

        assertThat(findMember1).isEqualTo(findMember2);
    }

    @Test
    @DisplayName("QueryDsl - 결과 조회")
    void resultFetch() {
        List<Member> result = queryFactory.selectFrom(member)
            .fetch();
        assertThat(result.size()).isEqualTo(4);

        Member findMember = queryFactory.selectFrom(member)
            .where(member.username.eq("member1"))
            .fetchOne();
        assertThat(findMember.getUsername()).isEqualTo("member1");

        findMember = queryFactory.selectFrom(member)
            .fetchFirst();
        assertThat(findMember.getUsername()).isEqualTo("member1");

        QueryResults<Member> results = queryFactory.selectFrom(member)
            .fetchResults();
        long total = results.getTotal();
        List<Member> content = results.getResults();
        assertThat(total).isEqualTo(4);
        assertThat(content.size()).isEqualTo(4);
        System.out.println("content = " + content);

        total = queryFactory.selectFrom(member)
            .fetchCount();
        assertThat(total).isEqualTo(4);
    }

    @Test
    @DisplayName("QueryDsl - 정렬")
    void sort() {
        em.persist(new Member(null, 0));

        // in()으로 username이 null인 객체가 조회되지 않는다. => isNull()을 사용해야 한다.
        List<Member> result = queryFactory.selectFrom(member)
            .where(member.username.in("member1", "member2", "member3").or(member.username.isNull()))
            .orderBy(member.age.desc(), member.username.asc().nullsLast())
            .fetch();

        assertThat(result.size()).isEqualTo(4);
        assertThat(result.get(0).getUsername()).isEqualTo("member3");
        assertThat(result.get(1).getUsername()).isEqualTo("member2");
        assertThat(result.get(2).getUsername()).isEqualTo("member1");
        assertThat(result.get(3).getUsername()).isEqualTo(null);
    }

    @Test
    @DisplayName("QueryDsl - 페이징1")
    void paging1() {
        List<Member> result = queryFactory.selectFrom(member)
            .orderBy(member.username.desc())
            .offset(0)
            .limit(3)
            .fetch();

        assertThat(result.size()).isEqualTo(3);
        assertThat(result.get(0).getUsername()).isEqualTo("member4");
        assertThat(result.get(1).getUsername()).isEqualTo("member3");
        assertThat(result.get(2).getUsername()).isEqualTo("member2");
    }

    @Test
    @DisplayName("QueryDsl - 페이징2")
    void paging2() {
        QueryResults<Member> results = queryFactory.selectFrom(member)
            .orderBy(member.username.desc())
            .offset(0)
            .limit(3)
            .fetchResults();

        assertThat(results.getTotal()).isEqualTo(4); // 페이징 유무와 상관없이 selectFrom()에 의해 조회되는 개수 반환
        assertThat(results.getResults().get(0).getUsername()).isEqualTo("member4");
        assertThat(results.getResults().get(1).getUsername()).isEqualTo("member3");
        assertThat(results.getResults().get(2).getUsername()).isEqualTo("member2");
    }

    @Test
    @DisplayName("QueryDsl - 집합 연산")
    void aggregation() {
        List<Tuple> result = queryFactory.select(
                member.count(),
                member.age.sum(),
                member.age.avg(),
                member.age.max(),
                member.age.min()
            ).from(member)
            .fetch();

        System.out.println("result = " + result);

        Tuple tuple = result.get(0);
        assertThat(tuple.get(member.count())).isEqualTo(4);
        assertThat(tuple.get(member.age.sum())).isEqualTo(100);
        assertThat(tuple.get(member.age.avg())).isEqualTo(25);
        assertThat(tuple.get(member.age.max())).isEqualTo(40);
        assertThat(tuple.get(member.age.min())).isEqualTo(10);
    }

    @Test
    @DisplayName("QueryDsl - groupBy")
    void groupBy() {
        List<Tuple> result = queryFactory.select(team.name, member.age.avg())
            .from(member)
            .join(member.team, team)
            .groupBy(team.name)
            .fetch();

        System.out.println("result = " + result);

        Tuple teamA = result.get(0);
        Tuple teamB = result.get(1);

        System.out.println("teamA = " + teamA);
        System.out.println("teamB = " + teamB);

        // get(select 절)
        assertThat(teamA.get(team.name)).isEqualTo("teamA");
        assertThat(teamA.get(member.age.avg())).isEqualTo(15);
        assertThat(teamB.get(team.name)).isEqualTo("teamB");
        assertThat(teamB.get(member.age.avg())).isEqualTo(35);
    }

    @Test
    @DisplayName("QueryDsl - inner join")
    void innerJoin() {
        //given
        //when
        List<Tuple> result = queryFactory.select(member, team)
            .from(member)
            .join(member.team, team)
            .where(team.name.eq("teamA"))
            .fetch();

        //then
        assertThat(result.get(0).get(member).getUsername()).isEqualTo("member1");
        assertThat(result.get(0).get(team).getName()).isEqualTo("teamA");

        assertThat(result.get(1).get(member).getUsername()).isEqualTo("member2");
        assertThat(result.get(1).get(team).getName()).isEqualTo("teamA");
    }

    @Test
    @DisplayName("QueryDsl - theta join")
    void thetaJoin() {
        //given
        em.persist(new Member("teamA"));
        em.persist(new Member("teamB"));

        //when
        List<Member> result = queryFactory.select(member)
            .from(member, team)
            .where(member.username.eq(team.name))
            .fetch();

        //then
        System.out.println("result = " + result);
        assertThat(result).extracting("username").containsExactly("teamA", "teamB");
    }

    @Test
    @DisplayName("QueryDsl - joinWithOn")
    void joinWithOn() {
        //given
        //when
        List<Tuple> result = queryFactory.select(member, team)
            .from(member)
            .leftJoin(member.team, team).on(team.name.eq("teamA"))
            .fetch();


        //then
        for (Tuple tuple : result) {
            System.out.println("tuple = " + tuple);
        }
    }

    /**
     * 연관관계가 없는 엔티티 외부 조인
     */
    @Test
    @DisplayName("QueryDsl - join_no_relation")
    void join_on_no_relation() {
        //given
        em.persist(new Member("teamA"));
        em.persist(new Member("teamB"));
        //when
        List<Tuple> result = queryFactory.select(member, team)
            .from(member)
            .leftJoin(team).on(member.username.eq(team.name)) // leftJoin(member.team, team) -> X (이것은 연관관계 조인)
            .fetch();

        //then
        for (Tuple tuple : result) {
            System.out.println("tuple = " + tuple);
        }
    }

    @Test
    @DisplayName("QueryDsl - compareJoin")
    void compare_join() {
        //given
        //when
        List<Tuple> result1 = queryFactory.select(member, team)
            .from(member)
            .leftJoin(member.team, team)
            .where(team.name.eq("teamA"))
            .fetch();

        List<Tuple> result2 = queryFactory.select(member, team)
            .from(member)
            .leftJoin(team).on(team.name.eq("teamA"))
            .fetch();

        List<Tuple> result3 = queryFactory.select(member, team)
            .from(member)
            .leftJoin(team).on(team.name.eq("teamA"))
            .fetch();

        //then
        System.out.println("result1 = " + result1);
        System.out.println("result2 = " + result2);
    }

    @PersistenceUnit
    EntityManagerFactory emf;

    @Test
    @DisplayName("QueryDsl - noFetchJoin")
    void no_fetch_join() {
        //given
        em.flush();
        em.clear(); // @BeforeEach로 인해 영속성 컨텍스트에 memer와 team이 관리되고 있음.
        //when
        Member findMember = queryFactory.selectFrom(member)
            .join(member.team, team)
            .where(member.username.eq("member1"))
            .fetchOne();

        boolean isProxyObject = emf.getPersistenceUnitUtil().isLoaded(findMember.getTeam());

        //then
        assertThat(isProxyObject).as("team은 proxy 객체이다").isFalse();
    }

    @Test
    @DisplayName("QueryDsl - useFetchJoin")
    void use_fetch_join() {
        //given
        em.flush();
        em.clear();

        //when
        Member findMember = queryFactory.selectFrom(member)
            .join(member.team, team).fetchJoin()
            .where(member.username.eq("member1"))
            .fetchOne();

        boolean isProxyObject = emf.getPersistenceUnitUtil().isLoaded(findMember.getTeam());

        //then
        assertThat(isProxyObject).as("team은 proxy 객체가 아니다.").isTrue();
    }

    @Test
    @DisplayName("QueryDsl - case문1")
    void basic_case() {
        //given
        //when
        List<Tuple> result = queryFactory.select(
                member.username,
                member.age.when(10).then("10살")
                    .when(20).then("20살")
                    .otherwise("20살 이상")
            ).from(member)
            .fetch();

        //then
        System.out.println("result = " + result);
    }

    @Test
    @DisplayName("QueryDsl - case문2")
    void complex_case() {
        //given
        //when
        List<Tuple> result = queryFactory.select(member.username,
                new CaseBuilder()
                    .when(member.age.between(10, 20)).then("10~20살")
                    .when(member.age.between(21, 30)).then("21~30살")
                    .otherwise("30살 이상")
            ).from(member)
            .fetch();

        //then
        System.out.println("result = " + result);
    }

    @Test
    @DisplayName("Querydsl - constant")
    void add_constant() {
        //given
        //when
        Tuple tuple = queryFactory.select(member.username, Expressions.constant("A"))
            .from(member)
            .where(member.username.eq("member1"))
            .fetchOne();

        //then
        System.out.println("tuple = " + tuple);
    }

    @Test
    @DisplayName("QueryDsl - add concat")
    void add_concat() {
        //given
        //when
        String result = queryFactory.select(member.username.concat("_").concat(member.age.stringValue()))
            .from(member)
            .where(member.username.eq("member1"))
            .fetchOne();
        //then
        assertThat(result).isEqualTo("member1_10");
    }
}
