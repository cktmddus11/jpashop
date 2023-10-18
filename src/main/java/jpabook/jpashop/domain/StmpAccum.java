package jpabook.jpashop.domain;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class StmpAccum {
    @Id
    @GeneratedValue
    @Column(name = "stmp_accum_rid")
    private Long rid;

    @OneToMany
    @JoinColumn(name = "stmp_round_rid")
    private List<StmpRound> stmpRoundList = new ArrayList<>();

    private LocalDateTime txnDt;

}
