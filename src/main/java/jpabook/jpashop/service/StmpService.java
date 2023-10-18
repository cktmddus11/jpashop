package jpabook.jpashop.service;

import jpabook.jpashop.domain.StmpMaster;
import jpabook.jpashop.repository.StmpRepository;
import jpabook.jpashop.service.model.StmpMasterDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class StmpService {
    private final StmpRepository stmpRepository;

    @Transactional // 영속성컨텍스트는 트랜젝션과 생명주기를 동일시함. 트렌젝션이 시작하지 않으면 영속성 컨텍스트도 없음. 없으면 persist시 오류 발생.
    public Long saveStmpMaster(StmpMasterDTO stmpMasterDto) {
        if (!stmpRepository.findAllStmpMasterByVaildDt(stmpMasterDto.getValidStrtDt(), stmpMasterDto.getValidEndDt(), stmpMasterDto.getRid()).isEmpty()) {
            throw new IllegalStateException("기간내 동록된 행사가 존재합니다.");
        }

        StmpMaster stmpMaster = null;

        if("S".equals(stmpMasterDto.getBusinesseType())){
             stmpMaster = StmpMaster.builder()
                    .stmpNo(stmpMasterDto.getStmpNo())
                    .name(stmpMasterDto.getName())
                    .accumCcnt(stmpMasterDto.getAccumCcnt())
                    .validStrtDt(stmpMasterDto.getValidStrtDt())
                    .validEndDt(stmpMasterDto.getValidEndDt())
                    .build();
            stmpRepository.save(stmpMaster);
        }else if("U".equals(stmpMasterDto.getBusinesseType())){
            StmpMaster findStmpMaster = this.findStmpOne(stmpMasterDto.getRid());
            stmpMaster = StmpMaster.builder()
                  //  .stmpNo(findStmpMaster.getStmpNo())
                    .name(findStmpMaster.getName())
                    .accumCcnt(findStmpMaster.getAccumCcnt())
                    .validStrtDt(findStmpMaster.getValidStrtDt())
                    .validEndDt(findStmpMaster.getValidEndDt())
                    .build();
        }


        return stmpMaster.getRid();
    }


    public List<StmpMasterDTO> findAllStmpMaster() {
        List<StmpMaster> stmpMasterDTOList = stmpRepository.findAllStmpMaster();
        return stmpMasterDTOList.stream().map(s -> StmpMasterDTO.builder()
                        .stmpNo(s.getStmpNo())
                        .name(s.getName())
                        .accumCcnt(s.getAccumCcnt())
                        .validStrtDt(s.getValidStrtDt())
                        .validEndDt(s.getValidEndDt())
                        .rid(s.getRid())
                        .build()
                )
                .collect(Collectors.toList());
    }

    public StmpMaster findStmpOne(Long stmpMasterRid) {
        return stmpRepository.findOneStmpMaster(stmpMasterRid);
    }
}
