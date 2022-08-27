package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberRepository {
    // 1.  회원 등록
    // 2. 회원 목록 조회

    @PersistenceContext
    private final EntityManager em;

//    public MemberRepository(EntityManager em) {
//        this.em = em;
//    }

    // 회원 등록
    public void save(Member member){
        em.persist(member);
    }
    // 회원 한명 조회
    public Member findOne(Long id){
        return em.find(Member.class, id);
    }

    // 회원 전체 조회
    public List<Member> findAll(){
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }

    // 이름으로 회원 목록 조회
    public List<Member> findByName(String name){
        return em.createQuery("select m from Member m where m.name = :name", Member.class)
                .setParameter("name", name)
                .getResultList();
    }

    /*public List<MemberService.MemberOrderDto> getMemberOrders(Integer memberId) {
    }*/
//    // 회원 객체 리턴?
//    public Member findAll2(String name){
//        return em.createQuery("select m from Member m where m.name = :name", Member.class)
//                .getResultList()
//                .stream().findAny().get();
//    }


}
