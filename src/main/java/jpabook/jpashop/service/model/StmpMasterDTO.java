package jpabook.jpashop.service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;


@Builder
@Data
@AllArgsConstructor
public class StmpMasterDTO {
    private Long rid;
    private String stmpNo;
    private String name;
    private Integer accumCcnt;
    private LocalDate validStrtDt;
    private LocalDate validEndDt;
    private String businesseType;

}
