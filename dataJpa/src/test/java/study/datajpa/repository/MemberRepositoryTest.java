package study.datajpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberRepositoryTest {

    @Autowired MemberRepository memberRepository;

    @Test
    void memberTest() throws Exception {
        //given
        Member member = new Member("memberA");

        //when
        Member savedMember = memberRepository.save(member);
        Member findMember = memberRepository.findById(savedMember.getId())
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 멤버를 찾을 수 없습니다."));

        //then
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember).isEqualTo(member);
    }

    @Test
    void basicCRUD() throws Exception {
        //given
        Member memberA = new Member("memberA");
        Member memberB = new Member("memberB");
        memberRepository.save(memberA);
        memberRepository.save(memberB);

        //when
        Member findMemberA = memberRepository.findById(memberA.getId())
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 멤버를 찾을 수 없습니다."));

        Member findMemberB = memberRepository.findById(memberB.getId())
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 멤버를 찾을 수 없습니다."));


        List<Member> all = memberRepository.findAll();
        long count = memberRepository.count();

        //then

        //단건 조회 검증
        assertThat(findMemberA).isEqualTo(memberA);
        assertThat(findMemberB).isEqualTo(memberB);

        //리스트 조회 검증
        assertThat(all.size()).isEqualTo(2);

        //카운트 검증
        assertThat(count).isEqualTo(2);

        //삭제 검증
        memberRepository.delete(memberA);
        memberRepository.delete(memberB);

        long deletedCount = memberRepository.count();
        assertThat(deletedCount).isEqualTo(0);
    }

    @Test
    void findByUsernameAndAgeGreaterThan() throws Exception {
        //given
        Member m1 = new Member("memberA", 10);
        Member m2 = new Member("memberA", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        //when
        List<Member> members = memberRepository.findByUsernameAndAgeGreaterThan("memberA", 15);

        //then
        assertThat(members.getFirst().getUsername()).isEqualTo("memberA");
        assertThat(members.getFirst().getAge()).isEqualTo(20);
        assertThat(members.size()).isEqualTo(1);
    }
}