package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/*
 * xToOne(ManyToOne, OneToOne)
 * Order
 * Order -> Member   = ManyToOne
 * Order -> Delivery  = OneToOne
 * */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;

    private final OrderSimpleQueryRepository orderSimpleQueryRepository;

    @GetMapping("/api/v1/simple-orders")
    public List<Order> orderV1() {
        List<Order> all = orderRepository.findAllByCriteria(new OrderSearch());
        for (Order order : all) {
            order.getMember().getName(); // Lazy 강제 초기화
            // order.getMember() 까지는 프록시 객체. getName 조회시
            // 실제로 쿼리를 날려 조회해옴. => Lazy 강제 초기화
            order.getDelivery().getAddress(); // Lazy 강제 초기화
        }
        return all;
    }
    @GetMapping("/api/v1_1/simple-orders")
    public List<Order> orderV1_1 (@RequestParam(value="orderId")  Long orderId){

        return orderRepository.getOrderAll(orderId);
    }


    // 엔티티가 그대로 리턴되지 않도록 DTO생성하여 리턴.
    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> orderV2() {
        List<Order> orders = orderRepository.findAllByCriteria(new OrderSearch());
        // orders select sql 1번 실행 => 출력 row수는 2건
        // 2번의 loop 돔. => 1건당 member, delivery 조회
        return orders.stream().map(o->new SimpleOrderDto(o)) // new SimpleOrderDto(o)
                .collect(Collectors.toList());
        // 총 5번의 쿼리가 돔. => N + 1 발생
        // N + 1 -> 1 orders + 회원 N(2) +  배송 N(2) => 5번
    }  // 그래도 ENGER로 하면 안됨. 양방향관계때문에 많은 조인을 일으킴.
        // 모든 연관관계는 Lazy 로 설정하고 필요하면 feth join 으로 성능 튜닝.

    // fetch join 을 이용한 N+1 문제 성능 최적화
    @GetMapping("/api/v3/simple-orders")
    public List<SimpleOrderDto> orderV3() {
        List<Order> orders = orderRepository.findAllWithMemberDelivery();
        return orders.stream().map(SimpleOrderDto::new)
                .collect(Collectors.toList());
    }
    // dto 로 바로 리턴하도록 수정 = v3과 차이점. select 절 시 dto의 생성자에서사용하는 필드만 select해옴. v3은 전체필드를 조회해옴.
    @GetMapping("/api/v4/simple-orders")
    public List<OrderSimpleQueryDto> orderV4() { // controller 가 repository dto에 의존하고 있긴한데 그대로 진행
        return orderSimpleQueryRepository.findOrderDtos();
    } // v3 vs v4 ? 어떤게 좋고 나쁘냐는 아님. v4는 재사용성이 적어짐. 정해진 dto에 있는 필드만을 조회하기 때문에
    // v3은 엔티티를 조회해오기 때문에 그 후에 가공하기 좋음.
    // 성능 최적화 부분으로는 v4가 낫긴함.



    @Data
    static class SimpleOrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDto(Order order){
            this.orderId = order.getId();
            this.name = order.getMember().getName(); // lazy 초기화.
            this.orderDate= order.getOrderDate();
            this.orderStatus = order.getStatus();
            this.address = order.getDelivery().getAddress();
        } // ORder => Member => Delivery 쿼리 실행
    }

}
