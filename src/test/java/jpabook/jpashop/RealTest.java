package jpabook.jpashop;

import jpabook.jpashop.domain.Order;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.service.OrderService;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class RealTest {
    @Autowired
    private EntityManager em;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;


    // 모든 주문사항을 조회하고 주문자의 이름을 출력한다.

    // N+1 문제를 확인한다.
    @Test
    @DisplayName("N+1문제 확인")
    public void NPlusOneTest(){
        System.out.println("+===========================");
        List<Order> orders = orderRepository.getOrderAll(null);
        System.out.println("+===========================");
        orders.stream().map(order -> order.getMember().getName())
                .forEach(System.out::println);
        System.out.println("+===========================");
    }

    @Test
    @DisplayName("fetch join 으로")
    public void fetchjoinTest(){
        List<Order> orders = orderRepository.getOrderAll2(null);
        orders.stream().map(order -> order.getMember().getName())
                .forEach(System.out::println);
    }


}
