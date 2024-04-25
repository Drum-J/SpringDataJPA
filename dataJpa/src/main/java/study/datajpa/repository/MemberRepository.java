package study.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.datajpa.entity.Member;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member,Long> {

    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    @Query(name = "Member.findByUsername") // 해당 어노테이션을 주석처리해도 잘 동작한다. Spring Data JPA의 관례를 따라
    // 해당 메서드의 이름을 가지고 있는 NamedQuery 가 있는지 먼저 찾아보기 때문.
    List<Member> findByUsername(@Param("username") String username);
}
