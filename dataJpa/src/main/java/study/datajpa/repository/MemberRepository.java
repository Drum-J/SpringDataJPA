package study.datajpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member,Long> {

    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    @Query(name = "Member.findByUsername") // 해당 어노테이션을 주석처리해도 잘 동작한다. Spring Data JPA의 관례를 따라
    // 해당 메서드의 이름을 가지고 있는 NamedQuery 가 있는지 먼저 찾아보기 때문.
    List<Member> findByUsername(@Param("username") String username);

    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    @Query("select m.username from Member m")
    List<String> findUsernameList();

    @Query("select new study.datajpa.dto.MemberDto(m.id , m.username, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();

    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") Collection<String> names);

    //위 @Query 어노테이션을 사용하지 않고 Spring Data JPA 가 알아서 만들어주는 메서드
    List<Member> findByUsernameIn(Collection<String> usernames);

    List<Member> findListByUsername(String username); //컬렉션
    Member findMemberByUsername(String username); //단건
    Optional<Member> findOptionalByUsername(String username); //단건 Optional

    @Query(value = "select m from Member m left join m.team t",
            countQuery = "select count(m) from Member m") // 카운트 쿼리를 따로 작성하는 방법
    Page<Member> findByAge(int age, Pageable pageable);

    Slice<Member> findSliceByAge(int age, Pageable pageable);
}
