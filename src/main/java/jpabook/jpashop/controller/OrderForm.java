package jpabook.jpashop.controller;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class OrderForm {
    private Long memberId;
    private Long itemId;
    private int count;
}
