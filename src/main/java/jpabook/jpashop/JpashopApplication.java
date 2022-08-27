package jpabook.jpashop;

import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class JpashopApplication {

	public static void main(String[] args) {
		/*Hello hello = new Hello();
		hello.setHello("hello~~");
		String helloPrint = hello.getHello();
		System.out.println("data = "+helloPrint);*/
		SpringApplication.run(JpashopApplication.class, args);
	}
	@Bean
	Hibernate5Module hibernate5Module(){
		Hibernate5Module hibernate5Module = new Hibernate5Module(); // json 로딩 시점에 Lazy 로딩 처리?
		// => Order안에있던 Member, OrderItem 등 Order 조회시점에  쿼리 다 날림.
		// 프록시객체를 null로 치환해주어 에러는 나지 않지만 api 스팩에 필요하지 않은 정보까지 모두 노출??
		//hibernate5Module.configure(Hibernate5Module.Feature.FORCE_LAZY_LOADING, true);
		return hibernate5Module;
	}

}
