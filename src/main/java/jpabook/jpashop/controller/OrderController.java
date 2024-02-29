package jpabook.jpashop.controller;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderSearch;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.service.ItemService;
import jpabook.jpashop.service.MemberService;
import jpabook.jpashop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final MemberService memberService;
    private final ItemService itemService;

    // 상품 주문 페이지 로 이동
    @GetMapping(value = "/order")
    public String createOrderForm(Model model){

        List<Member> members = memberService.findMembers();
        List<Item> items = itemService.findItems();

        model.addAttribute("members", members);
        model.addAttribute("items", items);

        return "order/orderForm";
    }

    // 상품 주문
    @PostMapping(value = "/order")
    public String order(
            @RequestParam("memberId") Long memberId
            , @RequestParam("itemId") Long itemId
            , @RequestParam("count") int count) {

        orderService.order(memberId, itemId, count);

        return "redirect:/orders";
    }

    // 주문 조회
    @GetMapping(value = "/orders")
    public String orderList(
            @ModelAttribute("orderSearch") OrderSearch orderSearch
            , Model model
            ){
        List<Order> orders = orderService.findOrders(orderSearch);
        model.addAttribute("orders", orders);

        return "order/orderList";
    }

    // 주문 취소
    @PostMapping(value = "/orders/{orderId}/cancel")
    public String cancelOrder(@PathVariable("orderId") Long orderId) {

        orderService.cancelOrder(orderId);

        return "redirect:/orders";
    }

    // 주문 삭제
}
