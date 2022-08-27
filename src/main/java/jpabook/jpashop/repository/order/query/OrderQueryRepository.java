package jpabook.jpashop.repository.order.query;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository { // 화면에 종속된 repository

    private final EntityManager em;


    public List<OrderQueryDto> findOrderQueryDtos() {
        List<OrderQueryDto> result = findOrders();  // query 1번 -> N개 (2개)
        result.forEach(o -> {
            List<OrderItemQueryDto> orderItems = findOrderItems(o.getOrderId()); // N번
            o.setOrderItems(orderItems);
        });
        return result;
    }
    // dto 로 조회하는 방식은 엔티티가 아니니까 fetch조인을 사용할 수없음..
    private List<OrderItemQueryDto> findOrderItems(Long orderId) {
        return em.createQuery("select new jpabook.jpashop.repository.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                        " from OrderItem oi" + // orderItem is not mapped error => 쿼리에서 클래스명과 동일하게 써야함.
                        " join oi.item i" + //   oi.Item i xXX  => could not resolve property: Item of: jpabook.jpashop.domain.OrderItem
                        " where oi.order.id = :orderId", OrderItemQueryDto.class)
                .setParameter("orderId", orderId)
                .getResultList();
    }

    private List<OrderQueryDto> findOrders() {
        return em.createQuery(  // 레포지토리가 api스팩에 의존하고 있음. => 화면에 의존.
                "select new jpabook.jpashop.repository.order.query.OrderQueryDto(o.id, m.name, o.orderDate, o.status, d.address)" +
                        " from Order o"+
                        " join o.member m"+
                        " join o.delivery d", OrderQueryDto.class
        ).getResultList();
    }
}
