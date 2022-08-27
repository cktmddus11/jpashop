package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

/*    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }*/

    /*
    * 회원 가입
    * */
    @Transactional
    public Long join(Member member){
        validateDuplicateMember(member); // 중복 회원 검증
        // 위에 검증 메서드에서 exception 발생시 여기서 트라이캐치 안하기 때문에 호출부로 
        // 에러 내용넘어감
        memberRepository.save(member);
        return member.getId();
    }


    private void validateDuplicateMember(Member member) {
        List<Member> findMembers = memberRepository.findByName(member.getName());
        /*if(!findMembers.isEmpty()){
            throw new IllegalStateException("이미 존재하는 회원입니다."); // 값의 중복일떄 쓰는 Excetion?
        }*/
        if(findMembers.size() > 0){
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    /*
    * 전체 회원 저회
    * */
    @Transactional(readOnly = false)
    public List<Member> findMembers(){
        return memberRepository.findAll();
    }

    @Transactional(readOnly = false)
    public Member findOne(Long memberId){
        return memberRepository.findOne(memberId);
    }

    @Transactional(readOnly = false)
    public void update(Long id, String name) { // 변경감지를 통해 처리
        Member findMember = memberRepository.findOne(id); // 영속상태의 Membeer 객체
        findMember.setName(name); // 값 변경시 변경 감지를 통해 transaction commit 순간에
        // jpa 가 flush 하며 영속성 컨텍스트에 commit 후 database까지 commit

        // 강사님 스타일로 update같은 경우는 void로 Member객체를 리턴하지 않음, id정도만 반환..
        // 커멘드와 쿼리를 분리하기 위해서???? Member 을 리턴한다는게 쿼리를 리턴한다는거????

    }

   /* public List<MemberOrderDto> getMemberOrders(Integer memberId) {
        return memberRepository.getMemberOrders(memberId);
    }*/

    @Data
    @AllArgsConstructor
    static class MemberOrderDto{
        private Long memberId;
        private int orderTotalPrice;
        private int orderCnt;

    }
}
