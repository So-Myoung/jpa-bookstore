package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class OrderSearch {
    private String memberName;
    private OrderStatus orderStatus; //주문 상태 [ORDER(주문), CANCEL(취소)]
}
