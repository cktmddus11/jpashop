package jpabook.jpashop.repository;

import jpabook.jpashop.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepository {


    @PersistenceContext
    private final EntityManager em;

    public void save(Item item){ // 있으면 기존거에 merge => 수정, 없으면 신규등록
        if(item.getId() == null){
            em.persist(item);
        }else {
            Item mergeItem = em.merge(item); // item 은 준영속 엔티티
        }                                   // mergeItem 영속성 엔티티
                                            // 결과적으로 영속성 엔티티를 반환하기 때문에
                                            // 변경감지가 작동하게된다.


    }


    // 아이템  단건 조회
    public Item findOne(Long itemId){
        return em.find(Item.class, itemId);
    }

    public List<Item> findAll(){
        return em.createQuery("select i from Item i", Item.class)
                .getResultList();
    }

}
