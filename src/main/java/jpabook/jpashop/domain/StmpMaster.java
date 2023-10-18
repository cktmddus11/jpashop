package jpabook.jpashop.domain;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class StmpMaster {

    @Id
    @GeneratedValue
    @Column(name="stmp_master_rid")
    private Long rid;

    private String stmpNo;

    private String name;

    private Integer accumCcnt;

    private LocalDate validStrtDt;
    private LocalDate validEndDt;

}
