package jpabook.jpashop.domain;

import lombok.Getter;


import javax.persistence.Embeddable;

@Embeddable // 어딘가에도 내장될 수 있다?
@Getter // setter없음
public class Address {
    private String city;
    private String street;
    private String zipcode;

    protected Address(){ // new로 생성 안하게 하려고 
    }
    // 값타입 생성자 이용해서 모두 초기화해서 변경불가능하게만듦.
    public Address(String city, String street, String zipcode) {
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }
}
