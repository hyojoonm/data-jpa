package study.datajpa.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id","username","age"}) // 출력하기 편하게 하기 위해서 연관관계 필드는 tostring 하지마라
@NamedQuery(
        name = "Member.findByUsername",
        query="select m from Member m where m.username = :username"
)
@NamedEntityGraph(name = "Member.all",attributeNodes = @NamedAttributeNode("team"))
public class Member  {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;
    private String username;
    private int age;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    public Member(String username, int age) {
        this.username = username;
        this.age = age;
    }

    public Member(String username) {
        this.username = username;
    }

    public Member(String username, int age, Team team) {
        this.username = username;
        this.age = age;
        if(team != null){
            changeTeam(team);
        }

    }

    // 연관관계 메서드 팀 변경  | 팀엔티티에 있는 멤버에도 이 팀을 추가함
    public void changeTeam(Team team){
        this.team = team;
        team.getMembers().add(this);
    }

    // JPA 기본스펙 중에 엔티티는 기본적으로 디폴트 생성자가 있어야함 파라미터 없이 프록시 같은 구현체를 쓸 때 프라이빗으로 막으면 막혀서
//    프로텍트로 열어두돼 함부로 못쓰게 막는다
//    protected Member(){
//
//    }
}
