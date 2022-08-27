package jpabook.jpashop.repository.order.simplequery;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderSimpleQueryRepository {

    @PersistenceContext
    private final EntityManager em;

    public List<OrderSimpleQueryDto> findOrderDtos() {
        return em.createQuery(  // 레포지토리가 api스팩에 의존하고 있음. => 화면에 의존.
                "select new jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto(o.id, m.name, o.orderDate, o.status, d.address) from Order o"+
                        " join o.member m"+
                        " join o.delivery d", OrderSimpleQueryDto.class
        ).getResultList();
    }
    // *** 강사님 팁
    // 레포지토리는 순수한 엔티티를 조회하는것으로 작성함
    // findOrderDtos 같은 경우은 repository 를 새로 추가해서 분리
}
