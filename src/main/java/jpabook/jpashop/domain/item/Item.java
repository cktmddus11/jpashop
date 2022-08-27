package jpabook.jpashop.domain.item;

import jpabook.jpashop.domain.Category;
import jpabook.jpashop.exception.NotEnoughStockException;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

//@BatchSize(size = 100) OrderItem - Item 의 컬렉션 미사용, toOne 관계에는 이렇게 작성해야함.
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE) // 상속 구현 전략 선택 => 상속관계, 싱글 테이블 설정
@DiscriminatorColumn(name="dtype") // 부모 클래스에 선언, 싱글테이블일떄 굳이 이 어노테이션 지정안해도 디폴트값임.
@Getter @Setter
public abstract class Item {
    @Id @GeneratedValue
    @Column(name="item_id")
    private Long id;


    private String name;

    private int price;

    private int stockQuantity;

    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList<>();

    // == 비즈니스 로직 == //
    public void addStock(int quantity){
        this.stockQuantity += quantity;
    }

    public void removeStock(int quantity) {
        int restStock = this.stockQuantity - quantity;
        if(restStock < 0){
            throw new NotEnoughStockException("need more stock");
        }
        this.stockQuantity -= quantity;
    }

    public  void changeItem(String name, int price, int stockQuantity){
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }
}


