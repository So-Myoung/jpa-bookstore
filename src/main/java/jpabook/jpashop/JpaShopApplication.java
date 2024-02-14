package jpabook.jpashop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JpaShopApplication {
	
	public static void main(String[] args) {
//		lombok 테스트
/*		Hello hello = new Hello();
		hello.setData("hello lombok");
		String data = hello.getData();
		System.out.println("data = " + data);*/

		SpringApplication.run(JpaShopApplication.class, args);
	}

}
