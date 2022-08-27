package jpabook.jpashop.controller;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @GetMapping("/members/new")
    public String createForm(Model model){

        model.addAttribute("memberForm", new MemberForm());
        return "members/createMemberForm";
    }

                                    // 실무에서 엔티티는 순수하게 유지해야함.화면과 종속되면안됨.
                                    // 엔티티는 핵심 비즈니스 로직 만 가지도록 하고.
                                    // 화면에 맞는 객체 DTO(getter, setter 가짐) 를 따로 사용
    @PostMapping("/members/new") // domain entity 에 유효성 검사를 넣으면 안됨. 새로 화면 폼 객체를 만들어서 처리하는게 좋음.
    public String createMember(@Valid MemberForm memberForm, BindingResult result){
        if(result.hasErrors()){
            return "members/createMemberForm";
        }

        Member member = new Member();
        member.setName(memberForm.getName());

        Address address = new Address(memberForm.getCity(), memberForm.getStreet(), memberForm.getZipcode());
        member.setAddress(address);
        memberService.join(member);
        return "redirect:/members";
    }

    @GetMapping("/members")
    public String list(Model model){ // 엔티티는 절대 외부로 노출되지 않는 것이 좋음 특히 API / 엔티티 필드 추가시 APi 스팩이 변경되버리기 때문에
        model.addAttribute("members", memberService.findMembers()); // 이부분도 dto를 따로 생성해서 사용하는것이 좋음.
        return "members/memberList";
    }




}
