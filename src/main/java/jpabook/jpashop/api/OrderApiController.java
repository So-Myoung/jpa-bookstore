package jpabook.jpashop.api;

import jpabook.jpashop.domain.*;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.order.query.OrderQueryDto;
import jpabook.jpashop.repository.order.query.OrderQueryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

/* 예제 : ToMany 관계도 존재 + 페이징 및 최적화
 OrderItem 및 Item 도메인 추가 사용
 컬렉션 조회 최적화
 */
@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;


    /**
     * V1. 엔티티 직접 노출
     * - Hibernate5Module 모듈 등록, LAZY=null 처리
     * - 양방향 관계 문제 발생 -> @JsonIgnore
     */
    @GetMapping("/api/orders/v1")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        for (Order order : all) {
            order.getMember().getName(); //Lazy 강제 초기화
            order.getDelivery().getAddress(); //Lazy 강제 초기화
            List<OrderItem> orderItems = order.getOrderItems();
            orderItems.stream().forEach(o -> o.getItem().getName()); //Lazy 강제 초기화
        }
        return all;
    }

    /**
     * V2. 엔티티를 조회해서 DTO로 변환(fetch join 사용X)
     * - 트랜잭션 안에서 지연 로딩 필요 -> 많은 SQL문 실행
     */
    @GetMapping("/api/orders/v2")
    public List<OrderDto> ordersV2(){
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
        return orders.stream()
                .map(o -> new OrderDto(o))
                .collect(toList());
    }

    /**
     * V3. 엔티티를 조회해서 DTO로 변환(fetch join 사용O), ToMany 관계까지 fetch join 하는 경우
     * - ToMany 관계가 있으므로 distinct를 써주어야 하며, 페이징이 불가능하다.
     * - oneToMany에서 one을 기준으로 페이징을 하는 것이 목적이지만, 데이터는 Many(N)를 기준으로 row가 생성
     * - 즉, Order를 기준으로 페이징 하고 싶은데, N인 OrderItem을 조인하면 OrderItem이 기준이 되어버림
     */
    @GetMapping("/api/orders/v3")
    public List<OrderDto> ordersV3() {
        List<Order> orders = orderRepository.findAllWithItem();
        return orders.stream()
                .map(OrderDto::new)
                .collect(toList());
    }

    /**
     * V3.1 엔티티를 조회해서 DTO로 변환 후 페이징 고려
     * - 방법: ToOne 연관 관계만 우선적으로 fetch join 후
     * - 지연 로딩 성능 최적화를 위해 hibernate.default_batch_fetch_size 혹은 @BatchSize 적용
     * - hibernate.default_batch_fetch_size: 글로벌 설정
     * - @BatchSize: 개별 최적화
     * - 하이버 네이트 6.2 : array_contains 사용
     * - where array_contains(?,item.item_id) : ?에 [] 배열이 들어감
     */
    @GetMapping("/api/orders/v3.1")
    public List<OrderDto> ordersV3_1(@RequestParam(value = "offset", defaultValue = "0") int offset
            ,@RequestParam(value = "limit", defaultValue = "100") int limit) {
        List<Order> orders = orderRepository.findAllWithMemberDeliveryPaging(offset, limit);
        return orders.stream()
                .map(OrderDto::new)
                .collect(toList());
    }

    /**
     * V4. JPA에서 DTO로 바로 조회, 컬렉션 N번 조회 (1 + N)
     * - 페이징 가능
     */
    @GetMapping("/api/orders/v4")
    public List<OrderQueryDto> ordersV4() {
        return OrderQueryRepository.findOrderQueryDtos();
    }


    /**
     * V5. JPA에서 DTO로 바로 조회, 컬렉션 1번 조회 최적화 버전 (1 + 1)
     * - 페이징 가능
     */


    /**
     * V6. JPA에서 DTO로 바로 조회, 플랫 데이터 조회 (1)
     * - 페이징 불가능
     */

    //-----------dto-----------
    @Data
    static class OrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate; //주문시간
        private OrderStatus orderStatus;
        private Address address;
        private List<OrderItemDto> orderItems;

        public OrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            orderStatus = order.getOrderStatus();
            address = order.getDelivery().getAddress();
            orderItems = order.getOrderItems().stream()
                    .map(item -> new OrderItemDto(item))
                    .collect(toList());
        }
    }

    @Data
    static class OrderItemDto {
        private String itemName;
        private int orderPrice;
        private int orderCount;

        public OrderItemDto(OrderItem orderItem) {
            itemName = orderItem.getItem().getName();
            orderPrice = orderItem.getOrderPrice();
            orderCount = orderItem.getCount();
        }
    }
}

