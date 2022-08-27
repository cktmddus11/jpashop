package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// 상품등록, 상품 목록 조회, 상품 수정
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    // 상품 등록, 수정
    @Transactional
    public void saveItem(Item item){
        itemRepository.save(item);
    }

    //1. setter사용하지 말고 엔티티안에서 바로 추척가능한 메서드를 만들어라?? => 비즈니스 변경로직을 엔티티안에 모아두는 느낌??
    //2.  어설프게 컨트롤러에 엔티티를 생성하지 말고 변경이 필요한 파라미터를(또는 넘길값이 많으면 Dto)
    // 서비스단으로 분명하게 넘겨서 사용하기 - 유지보수성 증가
    @Transactional
    public void updateItem(Long itemId, String name, int price, int stockQuantity) {
        Item findItem = itemRepository.findOne(itemId);  // 영속상태의 엔티티를 조회하고 
//        findItem.setName(name);  // 인테티의 데이터를 직접 변경하기
//        findItem.setPrice(price);
//        findItem.setStockQuantity(stockQuantity);
        findItem.changeItem(name, price, stockQuantity);
    }
   /* public void updateItem(Long itemId, Book param){
        Item findItem = itemRepository.findOne(itemId); // 영속성 엔티티를 불러오기 때문에
        findItem.setPrice(param.getPrice());
        findItem.setName(param.getName());
        findItem.setStockQuantity(param.getStockQuantity());  // 이렇게 setter보다 changer같은 메서드를 생성하는것이 좋다. 도메인 로직 설계 주의
        // 변경 후 save 처리를 할 필요 없음.
        // 영속성 컨텍스트가 커밋시점에 변경감지(Dirty Checking) 이 동작한다.

        // merge 동작방식은 이 코드에 return findItem 하는것과 동일하다.
        // 데이터를 바꿔치기 함.
    }*/

    // 상품 목록 조회
    public List<Item> findItems(){
        return itemRepository.findAll();
    }

    // 상품 단건 조회
    public Item findItem(Long itemId){
        return itemRepository.findOne(itemId);
    }



}
