package jpabook.jpashop.api;


import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor

public class OrderSimpleApi2Controller {

    private final OrderRepository orderRepository;

    private final OrderSimpleQueryRepository orderSimpleQueryRepository;


    @GetMapping("/api/v2/searchOrdereList")
    public List<Order> searchOrdereList() {
        List<Order> all = orderRepository.searchOrderList();
       /* for(Order order : all){
            order.getMember().getName();
            order.getDelivery().getAddress();
            List<OrderItem> orderItems = order.getOrderItems();
            orderItems.stream().forEach(o -> o.getItem().getName());
        }*/
        return all;
    }

    @GetMapping("/api/v2/searchOrdereList2")
    public List<OrderApiController.OrderDto> searchOrdereList2(@RequestParam(value="offset", defaultValue = "0") int offset,
                                                               @RequestParam(value="limit", defaultValue = "100") int limit) {
        List<Order> all = orderRepository.searchOrderList2(offset, limit); // member, delivery 는 fetch join 으로 이미 초기화되어
                                                                // 영속성 컨텍스트에 있음.
        List<OrderApiController.OrderDto> orderDto = all.stream()
                .map(OrderApiController.OrderDto::new)
                .collect(Collectors.toList());
        return orderDto;
    }
}
