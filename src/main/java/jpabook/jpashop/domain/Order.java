package jpabook.jpashop.domain;

import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name="orders") // sql에서 order 만 하면 안되니까 s붙이는거라서 이름 따로 정의
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class Order {  // Order와 양방향 연관관계에 있는 객체들을 @JsonIgnore을 이용해 끊어줌.
    @Id @GeneratedValue
    @Column(name="order_id")
    private Long id;

    // 연관관계 주인
    @ManyToOne(fetch = FetchType.LAZY) // 주문 기준 - 다 대 일 / ManyToOne 은 기본이 EAGER 이라 변경해주어야함.
    @JoinColumn(name="member_id") // EAGER 처리시 조인키 있는 테이블 다가져와짐. 100 + 1 ??? 주문이 백개가 나오면 엮긴 member 100 번 조회하게됨.
    private Member member; // 주문조회시 Member객체가 있긴하지만 Lazy 지연로딩이기때문에 db조회시
    // Order 객체와 매핑된 테이블만 가져오게된다.
                            // Member에 프록시 생성 클래스를  상속받아 프록시 객체를 생성한다.  => new ByteBuddyInterceptor
                            // member. 에 대해 조회시 그제서야 프록시 객체 조회하여 1차캐시에 있으면 그대로, 없으면
                            // 쿼리를 날려 member를 조회해온 후 1차캐싱한다..(=> 프록시 초기화)
    //@BatchSize(size = 1000) // application.yml에 적용한거는 global적용, 이거는 건별로 적용/ 컬렉션사용시 사용
    // 강사님은 보통 글로벌로 설정해서 사용하신다함.
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL , fetch= FetchType.LAZY) // oneToMany 는 기본적으로 fetch가 LAZY 처리되어있음.
    private List<OrderItem> orderItems  = new ArrayList<>();

    // CascadeType 엔티티의 상태변화를 전이시킬대 사용
    // 주문정보가 저장되면 배송 정보도 같이 저장되게. => order pertist 시 delivery 도 persist 하는 과정을 한번에 order persist로 처리
    @OneToOne(cascade = CascadeType.ALL, fetch=FetchType.LAZY) // CascadeType.All => 주문정보가 지워지면 배송정보도 같이 지워라
    @JoinColumn(name="delivery_id")
    private Delivery delivery;

    
    private LocalDateTime orderDate; // 주문시간

    @Enumerated(EnumType.STRING)
    private OrderStatus status; // 주문상태 [ORDER, CANCEL]

    // == 연관관계 편의 메서드 == //
    // 연관관계 주인에 있는게 좋음. 양방향 관계에서는 있는게 좋음.
    public void setMember(Member member){
        this.member = member;
        member.getOrders().add(this);
    }
   /* public static void main(String[] args){
        Member member = new Member();
        Order order = new Order();

        member.getOrders().add(order);
        order.setMember(member);
    }*/
    public void setDelivery(Delivery delivery){
        this.delivery = delivery;
        delivery.setOrder(this);
    }
    public void addOrderItem(OrderItem orderItem){
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void setOrderItem(OrderItem orderItem){
        this.orderItems.add(orderItem);
        orderItem.setOrder(this);
    }
    /* ===== 생성 메서드 =======*/
    public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems){
        Order order =new Order();
        order.setMember(member);
        order.setDelivery(delivery);
        for(OrderItem orderItem : orderItems){
            order.addOrderItem(orderItem);
        }
        order.setStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());
        return order;
    }
    // == 비즈니스 로직 == //
    /** 
     * 주문 취소
     **/
    public void cancel() {
        if(delivery.getStatus() == DeliveryStatus.COMP){
            throw new IllegalStateException("이미 배송완료된 상품은 취소가 불가능합니다.");
        }
        this.setStatus(OrderStatus.CANCEL);
        for(OrderItem orderItem : orderItems){ //this는 코딩스타일임.this.orderItmes
            orderItem.cancel();
        }
    }
    // == 조회 로직 == //
    /* 전체 주문 가격 조회 */
    public int getTotalPrice() {
//        int totalPrice = 0;
//        for(OrderItem orderItem : orderItems){
//            totalPrice += orderItem.getTotalPrice();
//        }
        int totalPrice = orderItems.stream()
                .mapToInt(OrderItem::getTotalPrice)
                .sum();
        return totalPrice;
    }
}
// 회원 - 주문 양방향 참조 관계
// 주문 - 배송 일대일 관계 시 FK를 어디에둬도 상관없게 되는데 주로 접근이 많이 이뤄지는곳에 두는것이 좋음
//    배송정보로 주문을 찾는거보다 주문에서 배송정보를 보는게 많으니까 주문에 FK설정(연관관계의 주인)