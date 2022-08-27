package jpabook.jpashop.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Member {
    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;
    @NotEmpty
    private String name;

    @Embedded // 내장타입을 포함하고 있다(둘중 한곳에만 붙여도 되는데 둘다 알아보기 쉽게 쓰는거)
    private Address address;

    // order에 의해 연관되어있을 뿐이야. 주문에 대해서 업데이트 쳐질떄 member은 읽기전용이 돼? 연관관계의 주인은 주문과 회원중  주문인듯.
    @JsonIgnore
    @OneToMany(mappedBy = "member") // 회원 기준 - 한 회원이 여러 주문 - 일 대 다
    //@JsonBackReference // 양방향 관계시 직렬화 제외처리하여 순환참조를 방지한다. => dto를 사용해야하는 이유
    // 필요한 데이터만을 옮겨담아 return 할수 있도록 dto를 사용하여 순환참조를 예방.
    private List<Order> orders = new ArrayList<>();
    // ** 컬렉션을 어떻게 초기화하는 게 좋을까? => 바로 초기화하는게 좋음
    // 하이버네티트 메커니즘에 문제 발생할 수 있음.



}
