package jpabook.jpashop.controller;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

@Getter @Setter
public class BookForm {
    private Long id;
    
    @NotEmpty(message =  "상품명을 입력하세요")
    private String name;
    @Min(value=3000, message = "최소금액은 3000 입니다.")
    private int price;
    private int stockQuantity;

    private String author;
    private String isbn;
}
