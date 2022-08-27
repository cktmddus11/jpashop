package jpabook.jpashop.service;

import jpabook.jpashop.domain.*;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import jpabook.jpashop.repository.MemberRepository;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    // 주문
    @Transactional
    public Long order(Long memberId, Long itemId, int count){
        // 엔티티 조회
        Member member = memberRepository.findOne(memberId);
        Item item = itemRepository.findOne(itemId);

        // 배송정보 생성
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());

        // 주문 상품 생성
        OrderItem orderItem = null;
        try{
            orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);
        }catch(Exception ex){
            return 0L;
        }

        //OrderItem orderItem1 = new OrderItem();   // 생성메서드 protected 처리, create메서드를 통해서면 생성하도록 유도

        // 주문 생성
        Order order = Order.createOrder(member, delivery, orderItem);

        // 주문 저장
        orderRepository.save(order); // 주문만 persist 하게되면 주문상품, 배송정보 둘 다  persist가 일어나게 된다
        // => ?? CascadeType.All 설정을 했기때문.
        // 주문을 지우면 주문 상품, 배송지 까지 삭제가 이루어짐 => 중요한 정보일떄는 Cascade 보다 개별적인
        // Repository 를 생성하여 각 각 persist 를 처리하는것이 좋다.
        
        return order.getId();
    }
    // 주문 취소
    @Transactional
    public void cancelOrder(Long orderId){
        // 주문정보 조회
        Order order = orderRepository.findOne(orderId);
        // 주문 취소
        order.cancel(); // JPA 가 엔티티내에서 변경된 변수들을
        // 더티체킹, 변경내역 감지하여 update 쿼리를 알아서 발생시켜줌.
        // 주문상태, 상품 수량 증가가 알아서 이루어짐.

    }

    // 검색
    public List<Order> findOrders(OrderSearch orderSearch){
        return orderRepository.findAllByCriteria(orderSearch);
    }


}
