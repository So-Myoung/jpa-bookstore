package jpabook.jpashop.api;

import jakarta.persistence.EntityManager;
import jakarta.validation.Valid;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderSearch;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryRepository;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

// 예제 : ToOne 관계만 존재, ToOne 일 시 fetch join 문제 없음.
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;
    private final OrderSimpleQueryRepository orderSimpleQueryRepository;
    private final EntityManager em;

    /*
    * V1. 엔티티 직접 노출
    * - Hibernate5Module 모듈 등록, LAZY=null 처리
    * - 양방향 관계 문제 발생 -> @JsonIgnore
    * */
    @GetMapping("/api/simple-orders/v1")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        for (Order order : all) {
            order.getMember().getName(); // Lazy 강제 초기화
            order.getDelivery().getAddress(); // Lazy 강제 초기화
        }
        return all;
    }

    /**
     * V2. 엔티티를 조회해서 DTO로 변환(fetch join 사용X)
     * - 단점: 지연로딩으로 쿼리 N번 호출
     */

    @GetMapping("/api/simple-orders/v2")
    public List<SimpleOrderDto> ordersV2(@RequestBody @Valid OrderSearchRequest request) {
        OrderSearch orderSearch = new OrderSearch();
        orderSearch.setMemberName(request.getMemberName());
        orderSearch.setOrderStatus(request.getOrderStatus());
        List<Order> orders = orderRepository.findAllByString(orderSearch);
        List<SimpleOrderDto> result = orders.stream()
                .map(SimpleOrderDto::new)
                .collect(toList());
        return result;
    }
    /*
    Request:
    {
        "memberName" : "userA",
        "orderStatus": "ORDER"
    }
    */

    /**
    * V3. 엔티티를 조회해서 DTO로 변환(fetch join 사용O), ToOne 관계만 fetch join
    * - fetch join으로 쿼리 1번 호출
    */
    @GetMapping("/api/simple-orders/v3")
    public List<SimpleOrderDto> ordersV3(@RequestBody @Valid OrderSearchRequest request) {
        List<Order> orders = orderRepository.findAllWithMemberDelivery();
        List<SimpleOrderDto> result = orders.stream()
                .map(SimpleOrderDto::new)
                .collect(toList());
        return result;
    }

    /**
     * V4. JPA에서 DTO로 바로 조회
     * - 쿼리 1번 호출
     * - select 절에서 원하는 데이터만 선택해서 조회
     */
    @GetMapping("/api/simple-orders/v4")
    public List<OrderSimpleQueryDto> ordersV4() {
        return orderSimpleQueryRepository.findOrderDtos();
    }

    //-----------dto-----------
    @Data
    static class OrderSearchRequest {
        private String memberName;
        private OrderStatus orderStatus;
    }

    @Data
    static class SimpleOrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate; //주문시간
        private OrderStatus orderStatus;
        private Address address;
        public SimpleOrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            orderStatus = order.getOrderStatus();
            address = order.getDelivery().getAddress();
        }
    }
}
