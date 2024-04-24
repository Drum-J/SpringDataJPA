package study.datajpa.repository;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class MemberJpaRepositoryTest {

    @Autowired MemberJpaRepository memberJpaRepository;

    @Test
    void testMember() throws Exception {
        //given
        Member member = new Member("memberA");

        //when
        Member savedMember = memberJpaRepository.save(member);
        Member findMember = memberJpaRepository.find(savedMember.getId());

        //then
        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
    }

    @Test
    void basicCRUD() throws Exception {
        //given
        Member memberA = new Member("memberA");
        Member memberB = new Member("memberB");
        memberJpaRepository.save(memberA);
        memberJpaRepository.save(memberB);

        //when
        Member findMemberA = memberJpaRepository.findById(memberA.getId())
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 멤버를 찾을 수 없습니다."));

        Member findMemberB = memberJpaRepository.findById(memberB.getId())
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 멤버를 찾을 수 없습니다."));


        List<Member> all = memberJpaRepository.findAll();
        long count = memberJpaRepository.count();

        //then

        //단건 조회 검증
        assertThat(findMemberA).isEqualTo(memberA);
        assertThat(findMemberB).isEqualTo(memberB);

        //리스트 조회 검증
        assertThat(all.size()).isEqualTo(2);

        //카운트 검증
        assertThat(count).isEqualTo(2);

        //삭제 검증
        memberJpaRepository.delete(memberA);
        memberJpaRepository.delete(memberB);

        long deletedCount = memberJpaRepository.count();
        assertThat(deletedCount).isEqualTo(0);
    }
}