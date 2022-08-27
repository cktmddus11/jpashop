package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.query.OrderQueryDto;
import jpabook.jpashop.repository.order.query.OrderQueryRepository;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class OrderApiController {
    final private OrderRepository orderRepository;
    final private OrderQueryRepository orderQueryRepository;



    // OneToMany 일대다 관계 (주문 - 주문아이템) 최적화 처리
    // 엔티티 직접 노출
    @GetMapping("/api/v1/orders")
    public List<Order> orderV1(){
        List<Order> all = orderRepository.findAllByCriteria(new OrderSearch());
        for(Order order : all){
            order.getMember().getName();  // 이름
            order.getDelivery().getAddress();
            
            List<OrderItem> orderItems = order.getOrderItems();
            orderItems.stream().forEach(o -> o.getItem().getName()); // Lazy 강제 초기화
            // hibernate5Module 기본값으로 설정되어있으면 프록시 객체는 데이터를 안뿌리게 되서
            // 강제로 데이터를 초기화 해주면 데이터를 뿌리게됨.
            // 양방향관계인 경우 한쪽에는 꼭 @JsonIgnore 붙이기.
        }
        return all;
    }
    // 엔티티 DTO로 변경
    @GetMapping("/api/v2/orders")
    public List<OrderDto> orderV2(){
        List<Order> orders = orderRepository.findAllByCriteria(new OrderSearch());
        List<OrderDto> collect = orders.stream().map(OrderDto::new) // new OrderDto(o)
                .collect(Collectors.toList());
        return collect;
    }
    //                       fetch join 사용으로 여러건나가던 쿼리가 쿼리 하나 실행으로 처리됨.
    // fetch join 으로 최적화.=> 이 처리를 통해 리포지토리단만 수정하면됨 아래 메서드단은 고칠게 없음.
    @GetMapping("/api/v3/orders")
    public List<OrderDto> orderV3(){
        List<Order> orders = orderRepository.findAllWithItem();

        for(Order order : orders){
            System.out.println("order ref = "+order+"orderId = "+order.getId());
        }

        List<OrderDto> collect = orders.stream().map(OrderDto::new) // new OrderDto(o)
                .collect(Collectors.toList());
        return collect;
    }
    // 위의 버전과 아래버전의 차이점. =>
    // 위처럼 fetch join 을 통해 하나의 쿼리로 한번에 가져온다는 장점이 있음.
    // 그러나 페이징이 불가능하기도 하고, 일대 다를 jetch join 을 하게 되면 데이터가 중복되면서
    // 데이터 전송량이 늘어나게 됨.

    // 아래 건은 중복건이 제거된 최적화된 쿼리로 전송하게 된다. => 데이터 전송량이 줄어듬.
    // 네트워크 호출하는 횟수와 네트워크 전송량것과 trade off가 발생한다.
    @GetMapping("/api/v3.1/orders") // order+member+deliver 1건 => orderitem 1건=> item 1 / item 2  * 2
    public List<OrderDto> orderV3_page (@RequestParam(value="offset", defaultValue = "0") int offset ,
                                        @RequestParam(value="limit", defaultValue = "100") int limit) {
        // toOne 관계는 fetch 조인으로 가져 오기
        List<Order> orders = orderRepository.findAllWithMemberDelivery(offset, limit);
        // 나머지는 지연로딩 프록시 초기화를 통해서 가져오기.
        return orders.stream()
                .map(OrderDto::new)
                .collect(Collectors.toList());
    } //  fetch join 컬렉션 성능 최적화 설정. 루프를 돌면서 미리떙겨와서  in 쿼리 처리
    // 최적화 후 item_id를 기준으로 in 절이기 때문에 쿼리 성능으로도 매우좋음. pk기준조회니까
    //hibernate에 최적화 되어있는 기능임. => order+member+deliver 1 => orderitem  1 => item 1 로 최적화 가능.

    @GetMapping("/api/v4/orders")
    public List<OrderQueryDto> ordersV4(){
        List<OrderQueryDto> result = orderQueryRepository.findOrderQueryDtos();
        return result;
    }



    @Data // @Getter만 해도됨.
    static class OrderDto { // DTO를 반환하라 => 코드상에서도 엔티티와 의존되지 않게 작성해야함. // 단 valueObject 같은 거는 그냥 노출해도됨. Address같은거
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;
        //private List<OrderItem> orderItems;
        private List<OrderItemDto> orderItems;

        public OrderDto(Order order){
            this.orderId = order.getId();
            this.name = order.getMember().getName(); // lazy 초기화.
            this.orderDate= order.getOrderDate();
            this.orderStatus = order.getStatus();
            this.address = order.getDelivery().getAddress();
//            order.getOrderItems().forEach(o -> o.getItem().getName());
//            this.orderItems = order.getOrderItems();  // ㅐorderItems 엔티티의 스팩이 다 노출되게 됨.
            this.orderItems = order.getOrderItems().stream() // dto를 하나더 만들어서 필요한 스팩만 노출되도록 감싸주기.
                    .map(OrderItemDto::new) // new orderItemDto(o)
                    .collect(Collectors.toList());
        }

    }
    @Getter
    static class OrderItemDto{
        private String itemName;
        private int orderPrice;
        private int count;


        public OrderItemDto(OrderItem orderItem){
            this.itemName = orderItem.getItem().getName();
            this.orderPrice = orderItem.getItem().getPrice();
            this.count = orderItem.getItem().getStockQuantity();
        }
    }


}
