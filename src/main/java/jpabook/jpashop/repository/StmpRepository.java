package jpabook.jpashop.repository;


import jpabook.jpashop.domain.StmpMaster;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class StmpRepository {
    @PersistenceContext
    private final EntityManager em;

    public List<StmpMaster> findAllStmpMasterByVaildDt(LocalDate validStrtDt, LocalDate validEndDt, Long rid) {
        String jpql = "";
        boolean isNew = rid == 0L;
        if (isNew) {
            jpql = "SELECT sm FROM StmpMaster sm " +
                    "WHERE sm.validStrtDt <= :endDt AND sm.validEndDt >= :startDt";
        } else { // 수정
            jpql = "SELECT sm FROM StmpMaster sm " +
                    "WHERE sm.validStrtDt <= :endDt AND sm.validEndDt >= :startDt and sm.rid != :rid";
        }

        TypedQuery<StmpMaster> query = em.createQuery(jpql, StmpMaster.class);
        query.setParameter("startDt", validStrtDt);
        query.setParameter("endDt", validEndDt);
        if (!isNew) {
            query.setParameter("rid", rid);
        }

        return query.getResultList();
    }

    public void save(StmpMaster stmpMaster) {
        em.persist(stmpMaster);
    }

    public List<StmpMaster> findAllStmpMaster() {
        return em.createQuery("select sm from StmpMaster sm", StmpMaster.class)
                .getResultList();
    }

    public StmpMaster findOneStmpMaster(Long stmpMasterRid) {
        return em.find(StmpMaster.class, stmpMasterRid);
    }

}
