package jpabook.jpashop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JpaShopApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(JpaShopApplication.class, args);
	}

//	@Bean
//	Hibernate5JakartaModule hibernate5Module() {
//		return new Hibernate5JakartaModule();
//	}
}
