package spring.querydsl;

import static org.assertj.core.api.Assertions.*;
import static spring.querydsl.entity.QMember.*;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;

import spring.querydsl.entity.Member;
import spring.querydsl.entity.QMember;
import spring.querydsl.entity.Team;

@SpringBootTest
@Transactional
public class QuerydslBasicTest {
	@PersistenceContext
	EntityManager entityManager;
	JPAQueryFactory queryFactory;

	@BeforeEach
	public void before() {
		queryFactory = new JPAQueryFactory(entityManager);

		Team teamA = new Team("teamA");
		Team teamB = new Team("teamB");
		entityManager.persist(teamA);
		entityManager.persist(teamB);

		Member member1 = new Member("member1", 10, teamA);
		Member member2 = new Member("member2", 20, teamA);
		Member member3 = new Member("member3", 30, teamB);
		Member member4 = new Member("member4", 40, teamB);

		entityManager.persist(member1);
		entityManager.persist(member2);
		entityManager.persist(member3);
		entityManager.persist(member4);
	}

	@Test
	public void startJPQL() {
		//member1 찾기, JPQL
		String qlString =
			"select m from Member m " +
				"where m.username = :username";

		Member findMember = entityManager.createQuery(qlString, Member.class)
			.setParameter("username", "member1")
			.getSingleResult();

		assertThat(findMember.getUsername()).isEqualTo("member1");
	}

	@Test
	public void startQuerydsl() {
		//member1 찾기, QueryDsl
		QMember m = new QMember("m");

		Member findMember = queryFactory
			.select(m)
			.from(m)
			.where(m.username.eq("member1"))
			.fetchOne();

		assertThat(findMember.getUsername()).isEqualTo("member1");
	}

	//검색 조건 쿼리
	@Test
	public void search() {
		Member findMember = queryFactory
			.selectFrom(member)
			.where(member.username.eq("member1")
				.and(member.age.eq(10)))
			.fetchOne();

		assertThat(findMember.getUsername()).isEqualTo("member1");
	}

	public void JPQL이_제공하는_모든_검색_조건_제공() {
		member.username.eq("member1"); // username = 'member1'
		member.username.ne("member1"); //username != 'member1'
		member.username.eq("member1").not(); // username != 'member1'

		member.username.isNotNull(); //이름이 is not null

		member.age.in(10, 20); // age in (10,20)
		member.age.notIn(10, 20); // age not in (10, 20)
		member.age.between(10, 30); //between 10, 30

		member.age.goe(30); // age >= 30
		member.age.gt(30); // age > 30
		member.age.loe(30); // age <= 30
		member.age.lt(30); // age < 30

		member.username.like("member%"); //like 검색
		member.username.contains("member"); // like ‘%member%’ 검색
		member.username.startsWith("member"); //like ‘member%’ 검색
	}

	//AND 조건을 파라미터로 처리, where() 에 파라미터로 검색조건을 추가하면 AND 조건이 추가됨
	@Test
	public void searchAndParam() {
		List<Member> result = queryFactory
			.selectFrom(member)
			.where(member.username.eq("member1"),
				member.age.eq(10))
			.fetch();

		assertThat(result.size()).isEqualTo(1);
	}

	/*	fetch() : 리스트 조회, 데이터 없으면 빈 리스트 반환
		fetchOne() : 단 건 조회
		결과가 없으면 : null
		결과가 둘 이상이면 : com.querydsl.core.NonUniqueResultException
		fetchFirst() : limit(1).fetchOne()
		fetchResults() : 페이징 정보 포함, total count 쿼리 추가 실행
		fetchCount() : count 쿼리로 변경해서 count 수 조회*/
	public void 결과조회() {
		//List
		List<Member> fetch = queryFactory
			.selectFrom(member)
			.fetch();

		//단건
		Member fetchOne = queryFactory
			.selectFrom(member)
			.fetchOne();

		//처음 한건 조회
		Member fetchFirst = queryFactory
			.selectFrom(member)
			.fetchFirst();

		//페이징에서 사용
		QueryResults<Member> results = queryFactory
			.selectFrom(member)
			.fetchResults();

		//count 쿼리로 변경
		long count = queryFactory
			.selectFrom(member)
			.fetchCount();
	}

	/**
	 *회원 정렬 순서
	 * 1. 회원 나이 내림차순(desc)
	 * 2. 회원 이름 올림차순(asc)
	 * 단 2에서 회원 이름이 없으면 마지막에 출력(nulls last) */
	@Test
	public void sort() {
		entityManager.persist(new Member(null, 100));
		entityManager.persist(new Member("member5", 100));
		entityManager.persist(new Member("member6", 100));

		//나이로 내림차순, 이름으로 오름차순, 이름이 null 일때 -> 마지막으로 출력
		List<Member> sortResult = queryFactory
			.selectFrom(member)
			.where(member.age.eq(100))
			.orderBy(member.age.desc(), member.username.asc().nullsLast())
			.fetch();

		Member member5 = sortResult.get(0);
		Member member6 = sortResult.get(1);
		Member memberNull = sortResult.get(2);

		assertThat(member5.getUsername()).isEqualTo("member5");
		assertThat(member6.getUsername()).isEqualTo("member6");
		assertThat(memberNull.getUsername()).isNull();
	}

	//조회 건수 제한
	@Test
	public void paging() {
		List<Member> paging = queryFactory
			.selectFrom(member)
			.orderBy(member.username.asc())
			.offset(0)
			.limit(2)
			.fetch();

		assertThat(paging.size()).isEqualTo(2);
		assertThat(paging.get(0).getUsername()).isEqualTo("member1");
		assertThat(paging.get(1).getUsername()).isEqualTo("member2");
	}

	//전체 조회 수
	/*	실무에서 페이징 쿼리를 작성할 때, 데이터를 조회하는 쿼리는 여러 테이블을 조인해야 하지만,
		count 쿼리는 조인이 필요 없는 경우도 있다. 그런데 이렇게 자동화된 count 쿼리는
		원본 쿼리와 같이 모두 조인을 해버리기 때문에 성능이 안나올 수 있다.
		count 쿼리에 조인이 필요없는 성능 최적화가 필요하다면, count 전용 쿼리를 별도로 작성해야 한다. */
	@Test
	public void paging2() {
		QueryResults<Member> queryResults = queryFactory
			.selectFrom(member)
			.orderBy(member.username.asc())
			.offset(0)
			.limit(3)
			.fetchResults();

		assertThat(queryResults.getLimit()).isEqualTo(3);
		assertThat(queryResults.getTotal()).isEqualTo(4);
		assertThat(queryResults.getOffset()).isEqualTo(0);
		assertThat(queryResults.getResults()).hasSize(3);
	}
}
