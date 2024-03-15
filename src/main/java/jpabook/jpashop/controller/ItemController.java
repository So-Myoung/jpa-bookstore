package jpabook.jpashop.controller;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.controller.form.BookForm;
import jpabook.jpashop.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    // 상품 등록 페이지 로 이동
    @GetMapping(value = "/items/new")
    public String createItemForm(Model model){
        model.addAttribute("itemForm", new BookForm());
        return "items/createItemForm";
    }

    // 상품 등록
    @PostMapping(value = "/items/new")
    public String createItem(BookForm bookForm) {
        Book book = new Book();

        book.setName(bookForm.getName());
        book.setPrice(bookForm.getPrice());
        book.setStockQuantity(bookForm.getStockQuantity());
        book.setAuthor(bookForm.getAuthor());
        book.setIsbn(bookForm.getIsbn());

        itemService.saveItem(book);
        return "redirect:/items";
    }

    // 상품 조회
    @GetMapping(value = "/items")
    public String itemList(Model model){
        List<Item> items = itemService.findItems();
        model.addAttribute("items", items);
        return "items/itemsList";
    }

    // 상품 수정 페이지 로 이동
    @GetMapping(value = "/items/{itemId}/edit")
    public String updateItemForm(
            @PathVariable("itemId") Long itemId, Model model){
        Book item = (Book) itemService.findOne(itemId);
        BookForm bookForm = new BookForm();

        bookForm.setId(item.getId());
        bookForm.setName(item.getName());
        bookForm.setPrice(item.getPrice());
        bookForm.setStockQuantity(item.getStockQuantity());
        bookForm.setAuthor(item.getAuthor());
        bookForm.setIsbn(item.getIsbn());

        model.addAttribute("itemForm", bookForm);
        return "items/updateItemForm";
    }

    // 상품 수정
    @PostMapping(value = "/items/{itemId}/edit")
    public String updateItem(@PathVariable("itemId") Long itemId
            , @ModelAttribute("itemForm") BookForm form) {

        itemService.updateItem(itemId, form.getName(), form.getPrice(), form.getStockQuantity());

        return "redirect:/items";
    }
}