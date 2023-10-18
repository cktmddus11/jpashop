package jpabook.jpashop.api;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.StmpMaster;
import jpabook.jpashop.service.MemberService;
import jpabook.jpashop.service.StmpService;
import jpabook.jpashop.service.model.StmpMasterDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.util.StringUtils;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController // @Controller + @ResponseBody => 데이터 자체를 바로 json, xml로 보낼떄 사용
@RequiredArgsConstructor
public class StmpApiController {

    private final StmpService stmpService;

    @PostMapping("/api/v2/stmpSave")
    public @ResponseBody CreateStmpMasterResponse saveStmpMaster(@RequestBody @Valid CreateStmpMasterRequest request){
        if("U".equals(request.getBusinesseType())
                && request.getStmpMasterRid() == 0L){
            throw new NullPointerException("필수값 확인필요");
        }

        StmpMasterDTO stmpMasterDTO = StmpMasterDTO.builder()
                .stmpNo(request.getStmpNo())
                .name(request.getName())
                .accumCcnt(request.getAccumCcnt())
                .validStrtDt(request.getValidStrtDt())
                .validEndDt(request.getValidEndDt())
                .businesseType(request.getBusinesseType())
                .rid(request.getStmpMasterRid())
                .build();


        Long stmpMasterRid = stmpService.saveStmpMaster(stmpMasterDTO);
        return new CreateStmpMasterResponse(stmpMasterRid);
    }


    @GetMapping("/api/v2/getStmpMasterList")
    public @ResponseBody Result getStmpMasterList(){
        List<StmpMasterDTO> stmpMasterDTOList = stmpService.findAllStmpMaster();
        return new Result(stmpMasterDTOList, stmpMasterDTOList.size());
    }
    
    
    
    


    @Data @AllArgsConstructor
    static class Result<T> {
        T data;
        int count;
    }

   /* @Data // @Getter @Setter @RequiredArgsConstructor @ToString @EqualsAndHashCode 모아둔 컴포넌트
    static class UpdateMemberRequest{
        private String name;
    }
    @Data
    @AllArgsConstructor
    static class UpdateMemberResponse{
        private Long id;
        private String name;
    }
*/
    @Data @AllArgsConstructor
    static class CreateStmpMasterResponse{
        private Long stmpMasterRid;
    }

    @Data
    static class CreateStmpMasterRequest {
        private Long stmpMasterRid;

        private String stmpNo;
        private String name;
        private Integer accumCcnt;
        private LocalDate validStrtDt;
        private LocalDate validEndDt;
        private String businesseType; // S 신규저장, U 변경
    }

}
