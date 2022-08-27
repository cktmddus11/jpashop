package jpabook.jpashop.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jpabook.jpashop.domain.item.Item;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // protected 생성자 처리와 동일
public class OrderItem {
    @Id @GeneratedValue
    @Column(name="order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="itme_id")
    private Item item; // 주문 상품

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="order_id")
    private Order order;

    private int orderPrice;  // 주문 가격  // orderPrice 테이블 생성시 스트링 부트가 자동으로 카멜케이스 변수를 언더스코어 로 변경해줌.
    private int count;  // 주문 수량

//    protected OrderItem(){
//
//    }
    public static OrderItem createOrderItem(Item item, int orderPrice, int count){
        // Item 에 price있는데 여기에 하는 이유 => 쿠폰 등등 할인으로 인해 실 결제금액은 다를 수 있으니까
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setOrderPrice(orderPrice);
        orderItem.setCount(count);

        item.removeStock(count);
        return orderItem;
    }



    // == 비즈니스 로직 == //
    public void cancel() {
        getItem().addStock(count);
    }
    // == 조회로직 == //
    /*
    * 주문 상품 전체 가격 조회
    * */
    public int getTotalPrice() {
        return getOrderPrice() * getCount();
    }
}
