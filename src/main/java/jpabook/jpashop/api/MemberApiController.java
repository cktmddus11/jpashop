package jpabook.jpashop.api;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController // @Controller + @ResponseBody => 데이터 자체를 바로 json, xml로 보낼떄 사용
@RequiredArgsConstructor
public class MemberApiController {
    private final MemberService memberService;

    @PostMapping("/api/v1/members")  //  RequestBody => json 데이터를 Member 객체로 변환해줌.
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member){
        // APi 스팩을 위한 별도의 Object-> DTO를 만들어서 사용해야 한다.
        // Member 객체를 바로 받게 되면 Member 엔티티의 필드명을 변경할 시 api 스팩이 변경되기 때문이다.
        // -> 엔티티를 파라미터로 받지 말자, 엔티티를 외부에 노출하지 말자.
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request){
        Member member = new Member();
        member.setName(request.getName());
        //Member 엔티티의 필드명이 변경되도 api 스팩과 매핑되는 객체인 CreateMemberRequest 와는 영향이 없다.
        Long memberId = memberService.join(member);
        return new CreateMemberResponse(memberId);
    }

    @PutMapping("/api/v2/members/{id}") // 등록과 수정은 api 스팩이 많이 다르기때문에 별도이 dto를 생성하는 것이 좋음.
    public UpdateMemberResponse updateMemberV2(@PathVariable("id") Long id,
                                               @RequestBody @Valid UpdateMemberRequest request){

        memberService.update(id, request.getName()); // update 는 변경감지가 되도록 짜는것이 좋음.

        Member findMember = memberService.findOne(id);
        return new UpdateMemberResponse(findMember.getId(), findMember.getName());
    }


    @GetMapping("/api/v1/members")  // Exception 발생 Cannot call sendError() after the response has been committed 순함참조관련
    public List<Member> membersV1(){ // 엔티티를 그대로 리턴하면 API 스팩을 그대로 노출하게됨.
        // api 스팩 변경됐다고 엔티티를 변경하게 되면 안됨;;;;dto롤 새로 생성해야함.
        // 유연성이 떨어짐. data 안에 array를 리턴하는것이 좋음 api추가 스팩이 존재하므로
        return memberService.findMembers();
    }

    @GetMapping("/api/v2/members")
    public Result membersV2(){
        List<Member> findMembers = memberService.findMembers();
        List<MemberDto> collect = findMembers.stream()
                .map(m -> new MemberDto(m.getName())) // m.getUserName 으로 엔티티가 추가되거나 변수명이 변경되어도
                .collect(Collectors.toList());      // 컴파일 단계에서 오류 방지 및 api 스팩이 변경되지 않음.

        return new Result(collect, collect.size());
    }



    //==========회원 주문 목록 조회===========//
    /*INPUT MEMBER_ID
     OUTPUT Result
        data    -- 주문 총 집계건수 리턴 
            MEMBER_ID
    *       ORDER_TOTAL_PRICE
            ORDER_CNT,
        -- 추후 주문 목록도 리턴할 수 있겠지
    * */
    //================================//
    /*@GetMapping("/api/memberOrders")
    public Result memberOrders(@RequestParam(value = "Id") Integer memberId){
        if(ObjectUtils.isEmpty(memberId)){
            return new Result(new ErrorDto("999", "member_id is null"), 0);
        }

        List<MemberOrderDto> memberOrders = memberService.getMemberOrders(memberId).;

        return new Result(memberOrders, memberOrders.size());
    }*/
   /* @Data @AllArgsConstructor
    static class ErrorDto{
        private String errorCode;
        private String errorDesc;
    }*/
    

    
    

    @Data @AllArgsConstructor
    static class MemberDto {
        private String name;
    }
    @Data @AllArgsConstructor
    static class Result<T> {
        T data;
        int count;
    }

    @Data // @Getter @Setter @RequiredArgsConstructor @ToString @EqualsAndHashCode 모아둔 컴포넌트
    static class UpdateMemberRequest{
        private String name;
    }
    @Data
    @AllArgsConstructor
    static class UpdateMemberResponse{
        private Long id;
        private String name;
    }

    @Data
    static class CreateMemberRequest{
        // 일부필드는 서비스 단에서 채워지는 값일 수도 있으니 개발자 입장에서는 Member 엔티티만 보고 어떤값을
        // 넘겨주어야 하는지 파악이 불가능하다.
        // api 스팩과 매핑되는 Object 를 만들면
        // 이 Object 만 파악하면 api 가 어떤값이 넘어오게 되는지 바로 파악 가능하다.
        private String name;
    }

    @Data  @AllArgsConstructor
    static class CreateMemberResponse {
        private Long id;

    }

}
