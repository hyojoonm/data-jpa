package study.datajpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member , Long> ,MemberRepositoryCustom {

    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    List<Member> findTop3HelloBy();

    /*
    주석 처리해도 실행이 되는 이유 : 관례상 JPA가 위에 JPArepository옆에 타입 Member.(메서드이름)findByUsername 을 찾아서 내가 등록
    안해도 이놈이 알아서 네임드 쿼리가 있으면 생성하고 없으면 메서드 쿼리 생성을 그대로 한다!
     */
//    @Query(name = "Member.findByUsername")
    List<Member> findByUsername(@Param("username") String username);

    // 어플리케이션이 로딩 할 떄 오류를 잡는다 (오타 같은) 정적 쿼리라서 문법 오류를 다 잡아내서 아주 좋음!!!


    @Query("select m.username from Member m")
    List<String> findUserNameList();

    @Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();

    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findUser(@Param("username") String username,@Param("age") int age);

    // 컬렉션도 파라미터 바인딩이 가능 위치 기반은 사용하지말고 이름 기반으로 파라미터 바인딩 사용
    @Query("select m from Member m where m.username in :names")
    List<Member> findBynames(@Param("names") Collection<String> names);


    // 반환타입이 굉장히 유연
    List<Member> findListByUsername(String username); // 컬랙션 조회
    Member findMemberByUsername(String username); // 단건 조회
    Optional<Member> findOptionalByUsername(String username); // 단건 Optional

    Page<Member> findByAge(int age, Pageable pageable);

    @Modifying(clearAutomatically = true) // Modifying 어노테이션에서 clearAutomatically 을 하면 메서드가 나가기전에 클리어를 하고 메서드를 실행함
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);

    @Query("select m from Member m left join fetch m.team")
    List<Member> findMemberFetchJoin();

    @Override
    @EntityGraph(attributePaths = {"team"})
    List<Member> findAll();

    // findAll 메서드랑 같음
    @EntityGraph(attributePaths = {"team"})
    @Query("select m from Member m")
    List<Member> findMemberEntityGraph();

    //    @EntityGraph("Member.all")
    @EntityGraph(attributePaths = {"team"})
    List<Member> findEntityGraphByUsername(@Param("username") String username);

    // 단순 조회용으로만 사용
    @QueryHints(value = @QueryHint( name ="org.hibernate.readOnly",value = "true"))
    Member findReadOnlyByUsername(String username);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Member> findLockByUsername(String username);
}
