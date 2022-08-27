package jpabook.jpashop.controller;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.service.ItemService;
import jpabook.jpashop.service.MemberService;
import jpabook.jpashop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class OrderController {
    final private OrderService orderService;
    final private MemberService memberService;
    final private ItemService itemService;

    @GetMapping("/order")
    public String createOrderForm(Model model){
        List<Member> members = memberService.findMembers();
        List<Item> items = itemService.findItems();
        model.addAttribute("members", members);
        model.addAttribute("items", items);
        model.addAttribute("count", 0);
        return "order/orderForm";
    }

    @PostMapping("/order")  //@RequestParam("memberId") Long memberId로 해도됨.
    public String createOrder(@Valid @ModelAttribute OrderForm orderForm, BindingResult result){
        // 식별키만 컨트롤러에서 넘기고 서비스단에서 영속성 엔티티를 서비스단에서 조회해서 쓰는것이 좋다.
        // Transactional 로 묶여있는 영속성 컨텍스트인 서비스단에서 처리하는것외 좋음.
        // 변경이 이루어져도 더티채킹 등을 해주니까
        Long orderId = orderService.order(orderForm.getMemberId(), orderForm.getItemId(), orderForm.getCount());
       /* if(orderId == 0L){
                result.addError(new ObjectError("count", "주문 가능 수량을 초과하였습니다."));
        }
        if(result.hasErrors()){
            return "order/orderForm";
        }*/
        //return "redirect:/orders/"+orderId;
        return "redirect:/orders";
    }


    @GetMapping("/orders")
    public String orderList(@ModelAttribute("orderSearch")OrderSearch orderSearch, Model model){
        List<Order> orders = orderService.findOrders(orderSearch);
        // ModelAttribute 를 하면 자동으로 model.addAttribute로 담기게 돼서
        // 굳이 추가할 필요없음.
        //  주문 검색을 한 후 자동으로  모델 객체에 담아서 주문검색결과가 화면에 뿌려지게됨.
        model.addAttribute("orders", orders);

        return "order/orderList";
    }

    @PostMapping("/orders/{orderId}/cancel")
    public String cancelOrder(@PathVariable("orderId") Long orderId){
        orderService.cancelOrder(orderId);
        return "redirect:/orders";
    }


}
