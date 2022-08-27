package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository { // 핵심 비즈니스로직이 있는 repository
    // 1. 상품 주문
    // 2. 주문 내역 취소
    // 3. 주문 취소
    @PersistenceContext
    private final EntityManager em;

    public void save(Order order) {
        em.persist(order);
    }

    public Order findOne(Long id) {
        return em.find(Order.class, id);
    }

    public List<Order> findAll(OrderSearch orderSearch){
        return em.createQuery("select o from Order" +
                " join o.member m" +
                " where o.status = :status "+
                " and m.name like :name", Order.class
        ).setParameter("status", orderSearch.getOrderStatus())
                .setParameter("name", orderSearch.getMemberName())
                .setMaxResults(1000) //최대 1000건
                .getResultList();
    }
    public List<Order> findAllByCriteria(OrderSearch orderSearch) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Order> cq = cb.createQuery(Order.class);
        Root<Order> o = cq.from(Order.class);
        Join<Order, Member> m = o.join("member", JoinType.INNER); // 회원과 조인

        List<Predicate> criteria = new ArrayList<>();

        // 주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            Predicate status = cb.equal(o.get("status"), orderSearch.getOrderStatus());
            criteria.add(status);
        }
        // 회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            Predicate name = cb.like(m.<String>get("name"), "%" + orderSearch.getMemberName() + "%");
            criteria.add(name);
        }
        cq.where(cb.and(criteria.toArray(new Predicate[criteria.size()])));
        TypedQuery<Order> query = em.createQuery(cq).setMaxResults(1000);  // setFirstResult 페이징?

        return query.getResultList();
    }

    public List<Order> findAllWithMemberDelivery() {
        return em.createQuery(
                "select o from Order o"+
                        " join fetch o.member m"+ // left join  으로 바꿔도됨.
                        " join fetch o.delivery d", Order.class // 이미 조인상태로 나오기 때문에 지연로딩일 일어나지 않음.
        ).getResultList();
    }


    // 네트워크 발달로 인해 네트워크로 인한 성능 차이는 크지 않으나
    // 상황에따라 적절히 사용할 필요가 있음
    // api 트래픽이 많은 상황이라면 아래껄 쓰는게 조금 더 나을 수 있고
    // 어드민 환경조회 정도라면 위에 것으로 사용해도 문제가 되지않음.
    /*public List<OrderSimpleQueryDto> findOrderDtos() {
        return em.createQuery(  // 레포지토리가 api스팩에 의존하고 있음. => 화면에 의존.
                "select new jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto(o.id, m.name, o.orderDate, o.orderStatus, d.address) from Order o"+
                " join o.member m"+
                " join o.delivery d", OrderSimpleQueryDto.class
                ).getResultList();
    }*/
    // *** 강사님 팁
    // 레포지토리는 순수한 엔티티를 조회하는것으로 작성함
    // findOrderDtos 같은 경우은 repository 를 새로 추가해서 분리

    public List<Order> findAllWithItem() {  //  distinct처리시 jpa 가 조회한 id값이 동일하면 둘중하나를 버리는 기능. => Order객체의 id가 동일하면 둘중하나삭제.
        return em.createQuery("select distinct o from Order o"+ // distinct 추가 중복제거 => db에서 쓰는 distinct 랑은 다름.
                                    " join fetch o.member m"+
                                    " join fetch o.delivery d "+ // 여기까지는 1대 1 관계라 괜찮은데
                                    " join fetch o.orderItems oi"+  // 1대 다관계 - 주문한건당 주문아이템이 두건이어서 주문과 조인을 하면
                                    " join fetch oi.item i", Order.class)
                .setFirstResult(0) // fetch join 사용했는데 페이징 처리시 경고나옴.
                .setMaxResults(100)  // => 일대다 조인이 된순간 모수는 다건에 해당되는 orderItem 에 맞춰서 페이지 처리를 하게 되는데
                .getResultList(); // 주문건이 2row가 발생함. 이를 중복제거처리                          //jpa 입장에서는 order 건을 페이징이불가능해짐..

        // 메모리 페이징 처리를 하게됨!!! 절대 일대 다 jetch조인에서는 페이징처리를 하면 안됨.일대일에서는 막 써도됨.
    } // 컬렉션 페치조인은 1개만 가능???? 그 이상으로 사용하면 데이터가 부정확해짐(뻥튀기 현상...? )


    public List<Order> findAllWithMemberDelivery(int offset, int limit) {
        return em.createQuery(
                "select o from Order o"+
                        " join fetch o.member m"+ // left join  으로 바꿔도됨.
                        " join fetch o.delivery d", Order.class // 이미 조인상태로 나오기 때문에 지연로딩일 일어나지 않음.
                        //"select o from Order o", Order.class //이렇게만 하면 되긴하는데 엔티티별로 단간의 쿼리를날려 in절로 조회해오긴하지만
                                                                    // toOne관계에서는 fetch조인으로 한번에 가져오는 것이 좋음. 이처럼 하면
                                                                    // 오히려 네트워크 전송량이 늘어남,날리는 쿼리가 많아지니까.
       ).setFirstResult(offset) // 0부터 시작.
                .setMaxResults(limit)
                .getResultList();
    }

    public List<Order> getOrderAll(Long orderId) {

      /*  return em.createQuery(
            "select o from Order o " +
                    "join o.member m " +
//                    "join o.delivery d " +
                    "where o.id := id", Order.class
        ).setParameter("id", orderId)
                .getResultList();*/
        return em.createQuery(
                        "select o from Order o " +
                                "join o.member m "+
                    "join o.delivery d "
                )
                .getResultList();
    }

    public List getOrderAll2(Long orderId) {
        return em.createQuery("select o from Order o " +
                "join fetch o.member").getResultList();
    }


}

