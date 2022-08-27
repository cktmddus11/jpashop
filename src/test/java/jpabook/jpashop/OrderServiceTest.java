package jpabook.jpashop;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.service.OrderService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
//@Rollback(value = false) // rollback 처리돼서 로그에 insert 안보이게됨.
public class OrderServiceTest {

    @Autowired
    private EntityManager em;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

//    @Autowired
//    private ItemRepository itemRepository;

    // 상품 주문
    @Test
    public void 상품주문() throws Exception{
        // given
        Member member = createMember();  // c + a + M 단축

        Book book = createBook("ORM 김영한", 10000, 10); // c + a + p

        int orderCount = 2;

        // when
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        //Item getItem = itemRepository.findOne(book.getId());

        // then
        Order getOrder =  orderRepository.findOne(orderId);
        Assert.assertEquals(orderId,getOrder.getId());
        Assert.assertEquals("상품 주문시 상태는 ORDER", OrderStatus.ORDER, getOrder.getStatus());
        Assert.assertEquals("주문한 상품 종류 수가 정확해야 한다.", 1, getOrder.getOrderItems().size());
        Assert.assertEquals("주문 가격은 가격 * 수량이다.", 10000 * orderCount, getOrder.getTotalPrice());
        Assert.assertEquals("주문 수량만큼 재고가 줄어야 한다.", 8, book.getStockQuantity());
        //Assert.assertEquals("상품 주문시 상품 수량이 감소해야한다.", 8, getItem.getStockQuantity());

    }



    // 주문 취소
    @Test
    public void 주문취소() throws Exception{
        // given
        Member member = createMember();
        Book item = createBook("boo1", 10000, 10); // c + a +v

        int orderCount = 2;
        Long orderId = orderService.order(member.getId(), item.getId(), orderCount);
        // when
        orderService.cancelOrder(orderId);

        // then
        // 검증내용 1. 재고 정상 복구, 2. 주문 상태 확인
        Order cancelOrder = orderRepository.findOne(orderId);
        Assert.assertEquals("재고 개수가 정상 복구 되어야한다. (10) ", 10,  item.getStockQuantity());
        Assert.assertEquals("주문 상태가 취소 처리 되어야한다.", OrderStatus.CANCEL, cancelOrder.getStatus());
    }
    // 상품 재고 수량 초과
    @Test(expected = NotEnoughStockException.class)
    //@Rollback(value = true)
    public void 상품주문_재고수량초과() throws Exception{
        // given
        Member member = createMember();
        Item item = createBook("ORM 김영한", 10000, 10);

        int orderCount = 10;
        // when
        orderService.order(member.getId(), item.getId(), orderCount);
        // then
        //Assert.assertThrows(RuntimeException.class,() ->   orderService.order(member.getId(), item.getId(), orderCount) );
        Assert.fail("재고 수량 부족 예외가 발생해야한다.");
    }


    private Book createBook(String name, int price, int stockQuantity) {
        Book book = new Book();
        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(stockQuantity);
        em.persist(book);
        return book;
    }

    private Member createMember() {
        Member member = new Member();
        member.setName("회원1");
        member.setAddress(new Address("서울", "진흥로7가길", "3-17"));
        em.persist(member);
        return member;
    }

}
