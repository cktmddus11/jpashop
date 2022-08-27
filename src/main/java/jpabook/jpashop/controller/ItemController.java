package jpabook.jpashop.controller;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping("/items/new")
    public String createItem(Model model){
        model.addAttribute("form", new BookForm());
        return "items/createItemForm";
    }

    // 리펙토링 필요 요소 1 validation 처리  안했음. 2 book setter사용보다 static create메서드로 하나 만드는게 좋음. setter 제거
    @PostMapping("/items/new")
    public String createItem(@ModelAttribute("form") @Valid BookForm form, BindingResult result){
        if(result.hasErrors()){
            return "items/createItemForm";
        }

        Book book = new Book();
        book.setName(form.getName());
        book.setPrice(form.getPrice());
        book.setStockQuantity(form.getStockQuantity());
        book.setAuthor(form.getAuthor());
        book.setIsbn(form.getIsbn());

        itemService.saveItem(book);

        return "redirect:/items";
    }

    @GetMapping("/items")
    public String list(Model model){
        model.addAttribute("items", itemService.findItems());
        return "items/itemList";
    }


    @GetMapping("items/{itemId}/edit")
    public String updateItemForm(Model model, @PathVariable("itemId") Long id){
        Book item = (Book) itemService.findItem(id);

        BookForm form = new BookForm();
        form.setId(item.getId());
        form.setName(item.getName());
        form.setPrice(item.getPrice());
        form.setStockQuantity(item.getStockQuantity());
        form.setAuthor(item.getAuthor());
        form.setIsbn(item.getIsbn());

        model.addAttribute("form", form);
        return "items/updateItemForm";
    }

    @PostMapping("items/{itemId}/edit")
    public String updateItem(@ModelAttribute("form") BookForm form, @PathVariable("itemId") Long itemId){

        // new Book() 로 생성한 book 객체는 은 준영속성 엔티티에 속한다.(임의로 생성한 엔티티이기도 하며 식별자가 존재하기 때문)
        // => 이미 디비에 한번 저장되어 id가 존재하기떄문에 영속성 컨텍스트가 관리하지 않음.
        // => 변경 감지를 하지 않는다.
//        Book book = new Book();
//        book.setId(form.getId()); // front 아이디 변경 주의 필요, 변경권한 확인 로직 필요.
//        book.setPrice(form.getPrice());
//        book.setName(form.getName());
//        book.setStockQuantity(form.getStockQuantity());
//        book.setAuthor(form.getAuthor());
//        book.setIsbn(form.getIsbn());


        // ** 실무에서 merge를 무조건적으로  사용하면 안된다.
        // 만약 가격을 수정 불가하게 설정한다고 하면 book객체에 price set하는 부분을 삭제하게되면
        // null값이 들어가게 되고 null로 merge처리가 되어버린다.
        // 실무는 변경감지를 이용하는것이 좋다..

        //itemService.saveItem(book);
        itemService.updateItem (itemId, form.getName(), form.getPrice(), form.getStockQuantity());
        return "redirect:/items";
    }

}
