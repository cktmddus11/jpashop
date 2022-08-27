package jpabook.jpashop.repository.order.query;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        return em.createQuery("select " +
                        "new jpabook.jpashop.repository.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                        " from OrderItem oi" + // orderItem is not mapped error => 쿼리에서 클래스명과 동일하게 써야함.
                        " join oi.item i" + //   oi.Item i xXX  => could not resolve property: Item of: jpabook.jpashop.domain.OrderItem
                        " where oi.order.id = :orderId", OrderItemQueryDto.class)
                .setParameter("orderId", orderId)
                .getResultList();
    }

    private List<OrderQueryDto> findOrders() {
        return em.createQuery(  // 레포지토리가 api스팩에 의존하고 있음. => 화면에 의존.
                "select " +
                        "new jpabook.jpashop.repository.order.query.OrderQueryDto(o.id, m.name, o.orderDate, o.status, d.address)" +
                        " from Order o" +
                        " join o.member m" +
                        " join o.delivery d", OrderQueryDto.class
        ).getResultList();
    }


    /**
     * 최적화
     * Query : 루트 1번, 컬렉션 1번
     * 데이터를 한꺼번에 처리할 때 많이 사용하는 방식
     * 
     * @return 
     */
    public List<OrderQueryDto> findAllByDto_optimization() {
        // 루트 조회(toOne 코드를 모두 한번에 조회)
        List<OrderQueryDto> result = findOrders();
        // orderItem 컬렉션을 Map 한방에 조회
        Map<Long, List<OrderItemQueryDto>> orderItemMap = findOrderItemMap(result);
        result.forEach(r -> r.setOrderItems(orderItemMap.get(r.getOrderId())));
        return result;
    }

    private Map<Long, List<OrderItemQueryDto>> findOrderItemMap(List<OrderQueryDto> result) {
        List<OrderItemQueryDto> orderItems = em.createQuery("select " +
                        "new jpabook.jpashop.repository.order.query.OrderItemQueryDto(" +
                        "oi.order.id, i.name, oi.orderPrice, oi.count" +
                        ")" +
                        " from OrderItem oi" +
                        " join oi.item i" +
                        " where oi.order.id in :orderIds", OrderItemQueryDto.class)
                .setParameter("orderIds", toOrderIds(result))
                .getResultList();

        // 메모리를 통해서 처리해서 쿼리를 2번만에 처리하게 됨 => orderList 한번, orderItem 한번
        Map<Long, List<OrderItemQueryDto>> orderItemMap = orderItems.stream()
                .collect(Collectors.groupingBy(OrderItemQueryDto::getOrderId));
        return orderItemMap;
    }

    private List<Long> toOrderIds(List<OrderQueryDto> result) {
        List<Long> orderIds = result.stream()
                .map(OrderQueryDto::getOrderId)
                .collect(Collectors.toList());
        return orderIds;
    }

    public List<OrderFlatDTO> findAllByDto_flat() {
        return em.createQuery("select " +
                        "new jpabook.jpashop.repository.order.query.OrderFlatDTO(" +
                        "o.id, m.name, o.orderDate, o.status, d.address, " +
                        "i.name, oi.orderPrice, oi.count" +
                        ") " +
                        "from Order o " +
                        "join o.member m " +

                        "join o.delivery d " +
                        "join o.orderItems oi " +
                        "join oi.item i", OrderFlatDTO.class)
                .getResultList();
    }
}
