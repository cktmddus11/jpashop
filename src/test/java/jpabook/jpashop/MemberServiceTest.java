package jpabook.jpashop;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import jpabook.jpashop.service.MemberService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

@RunWith(SpringRunner.class) // 스프링 테스트와 통합
@SpringBootTest // springboot띄우고 테스트를 할때 사용, 없으면 autowired다 실패
@Transactional
public class MemberServiceTest {

    @Autowired
    MemberService memberService;

    //@Autowired
    //MemberRepository memberRepository;

    @Autowired
    EntityManager entityManager;

    @Test
    @Rollback(false) // 트랜젝션 어노테이션 테스트에 사용 시 기본적으로 롤백 값
    // persist 하고 영속성컨텍스트를 flush를 해야지 디비에 insert되는건데
    // rollback처리하면 flush를 안함.
    public void 회원가입() throws Exception {
        //given
        Member member = new Member();
        member.setName("kio");
        //when
        Long saveId = memberService.join(member);
        //then
       //entityManager.flush();
        Assert.assertEquals(member, memberService.findOne(saveId));
        // 같은 트랜젝션안에서는
        // 영속성 컨텍스트에 있는 동일한 id 로 된 객체를 조회해오기 때문에 같다고 출력
    }
    @Test(expected = IllegalStateException.class)
    public void 중복_회원_예외() throws Exception {
        //given
        Member member1 = new Member();
        member1.setName("Cha");

        Member member2 = new Member();
        member2.setName("Cha");

        //when
        memberService.join(member1);
        memberService.join(member2);
       /* try {
            memberService.join(member2); // 예외가 발생해야한다.
            // 에러발생으로 여기서 테스트 중단처리
        }catch(IllegalStateException e){
            return;
        }*/

        //then
        Assert.fail("예외가 발생해야한다.");


    }
    
}
