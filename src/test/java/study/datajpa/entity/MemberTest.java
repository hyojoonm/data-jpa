package study.datajpa.entity;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.repository.MemberRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberTest {

    @PersistenceContext
    EntityManager em;

    @Autowired
    MemberRepository memberRepository;

    @Test
    public void testEntity(){
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("meber1",10,teamA);
        Member member2 = new Member("meber2",20,teamA);
        Member member3 = new Member("meber3",30,teamB);
        Member member4 = new Member("meber4",40,teamB);

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        // 초기화
        // 강제로 DB에 인설트 쿼리를 날림
        em.flush();
        em.clear(); // 영속성 컨텍스트에 있는 캐시를 날림

        //확인
        List<Member> members = em.createQuery("select m from Member m", Member.class).getResultList();

        for (Member member : members) {
            System.out.println("member = " + member);
            System.out.println("member.team = " + member.getTeam());
        }
    }

    @Test
    public void JpaEventBaseEntity() throws Exception{
        //given
        Member member = new Member("member1");
        memberRepository.save(member);

        Thread.sleep(100);
        member.setUsername("member2");

        em.flush(); //PreUpdate 메서드 나감
        em.clear();
        //when
        Member findMember = memberRepository.findById(member.getId()).get();
        //then
//        System.out.println("findMember = " + findMember.getCreatedDate());
//        System.out.println("findMember.getUpdatedDate() = " + findMember.getLastModifiedDate());
//        System.out.println("findMember.getCreatedBy() = " + findMember.getCreatedBy());
//        System.out.println("findMember.getLastModifiedBy() = " + findMember.getLastModifiedBy());
    }
}