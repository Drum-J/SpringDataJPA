package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class MemberRepositoryTest {

    @Autowired MemberRepository memberRepository;
    @Autowired TeamRepository teamRepository;

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

    @Test
    void findByUsername() throws Exception {
        //given
        Member m1 = new Member("memberA", 10);
        Member m2 = new Member("memberB", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        //when
        List<Member> result = memberRepository.findByUsername("memberA");

        //then
        Member find = result.get(0);
        assertThat(find).isEqualTo(m1);
    }

    @Test
    void findUser() throws Exception {
        //given
        Member m1 = new Member("memberA", 10);
        Member m2 = new Member("memberB", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        //when
        List<Member> result = memberRepository.findUser("memberA", 10);

        //then
        assertThat(result.getFirst()).isEqualTo(m1);
    }

    @Test
    void findUsernameList() throws Exception {
        //given
        Member m1 = new Member("memberA", 10);
        Member m2 = new Member("memberB", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        //when
        List<String> usernameList = memberRepository.findUsernameList();

        //then
        assertThat(usernameList).containsExactly("memberA", "memberB");
    }

    @Test
    void findMemberDto() throws Exception {
        //given
        Team teamA = new Team("teamA");
        teamRepository.save(teamA);

        Member m1 = new Member("memberA", 10, teamA);
        memberRepository.save(m1);

        //when
        List<MemberDto> memberDto = memberRepository.findMemberDto();

        //then
        assertThat(memberDto.getFirst().getId()).isEqualTo(m1.getId());
        assertThat(memberDto.getFirst().getUsername()).isEqualTo(m1.getUsername());
        assertThat(memberDto.getFirst().getTeamName()).isEqualTo(teamA.getName());
    }

    @Test
    void findByNames() throws Exception {
        //given
        Member m1 = new Member("memberA", 10);
        Member m2 = new Member("memberB", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        //when
        List<Member> result = memberRepository.findByNames(Arrays.asList("memberA", "memberB"));

        //then
        assertThat(result).containsExactly(m1, m2);
    }

    @Test
    void findByUsernameIn() throws Exception {
        //given
        Member m1 = new Member("memberA", 10);
        Member m2 = new Member("memberB", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        //when
        List<Member> result = memberRepository.findByUsernameIn(Arrays.asList("memberA", "memberB"));

        //then
        assertThat(result).containsExactly(m1, m2);
    }

    @Test
    void resultType() throws Exception {
        //given
        Member m1 = new Member("memberA", 10);
        Member m2 = new Member("memberB", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        //when
        List<Member> listResult = memberRepository.findListByUsername("memberA");
        Member findMember = memberRepository.findMemberByUsername("memberA");
        Optional<Member> optionalMember = memberRepository.findOptionalByUsername("memberA");

        //then
        assertThat(listResult).containsExactly(m1);
        assertThat(findMember).isEqualTo(m1);
        assertThat(optionalMember.orElse(null)).isEqualTo(m1);
    }

    @Test
    void paging() throws Exception {
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));
        memberRepository.save(new Member("member6", 10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        //when
        Page<Member> page = memberRepository.findByAge(age, pageRequest); // 반환타입이 Page 라서 count 쿼리를 알아서 한 번 더 날림.

        // DTO로 변환
        Page<MemberDto> toMap = page.map(member -> new MemberDto(member.getId(), member.getUsername(), null/*member.getTeam().getName()*/));
        for (MemberDto memberDto : toMap) {
            System.out.println("memberDto = " + memberDto);
        }

        //then
        List<Member> content = page.getContent();
        long totalElements = page.getTotalElements();

        assertThat(content.size()).isEqualTo(3);
        assertThat(totalElements).isEqualTo(6);
        assertThat(page.getNumber()).isEqualTo(0); // 현재 페이지 넘버
        assertThat(page.getTotalPages()).isEqualTo(2); // 총 페이지
        assertThat(page.isFirst()).isTrue(); // 첫번째 페이지냐?
        assertThat(page.hasNext()).isTrue(); // 다음 페이지가 있냐?
    }

    @Test
    void slice() throws Exception {
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));
        memberRepository.save(new Member("member6", 10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        //when
        Slice<Member> page = memberRepository.findSliceByAge(age, pageRequest); // Slice 를 사용하게 되면 PageRequest 에서 3개를 요청했으나 +1 해서 4개를 가져온다.

        //then
        List<Member> content = page.getContent();

        assertThat(content.size()).isEqualTo(3); // 4개를 가져오지만 content는 내가 요청한 3개만 있다는 것을 알 수 있다.
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();
    }
}