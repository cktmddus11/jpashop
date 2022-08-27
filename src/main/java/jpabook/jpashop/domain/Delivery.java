package jpabook.jpashop.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
public class Delivery {
   @Id @GeneratedValue
   @Column(name="delivery_id")
   private Long id;

   @JsonIgnore
   @OneToOne(mappedBy = "delivery", fetch=FetchType.LAZY)
   private Order order;

   private Address address;

    // ORDINAL 는 각 상태값이 번호로 들어가게 되는데 상태값이 중간에 추가된다하면 번호가 꼬이기 때문.
    // STRING 은 그대로 문자로 들어가게됨.
   @Enumerated(EnumType.STRING) // 이 어노테이션 기본값이 ORDINAL 로 설정되어있으므로 EnumType를 바꿔주어야한다.
   private DeliveryStatus status; // READY, CANCEL
}
