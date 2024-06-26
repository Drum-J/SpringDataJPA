package study.datajpa.entity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.repository.MemberRepository;

import java.util.List;

@SpringBootTest
@Transactional
class MemberTest {

    @PersistenceContext
    EntityManager em;

    @Autowired
    MemberRepository memberRepository;

    @Test
    void testEntity() throws Exception {
        //given
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

        // 초기화
        em.flush(); // flush 를 호출해서 강제로 DB에 Insert 쿼리를 날린다.
        em.clear(); // 영속성 컨텍스트의 캐쉬를 다 날린다.

        //when
        List<Member> members = em.createQuery("select m from Member m", Member.class).getResultList();

        //then
        for (Member member : members) {
            System.out.println("member = " + member);
            System.out.println("-> member.team = " + member.getTeam());
        }
    }

    @Test
    @Rollback(value = false)
    void jpaEventBaseEntity() throws Exception {
        //given
        Member member = new Member("member1", 10);
        memberRepository.save(member); //@PerPersist 발생

        Thread.sleep(100);
        member.changeUsername("memberUpdate");

        em.flush(); //@PreUpdate
        em.clear();

        //when
        Member findMember = memberRepository.findById(member.getId()).get();

        //then
        System.out.println("createdDate = " + findMember.getCreatedDate());
        //System.out.println("updatedDate = " + findMember.getUpdatedDate());
        System.out.println("updatedDate = " + findMember.getLastModifiedDate());
        System.out.println("createdBy = " + findMember.getCreatedBy());
        System.out.println("lastModifiedBy = " + findMember.getLastModifiedBy());
    }

}