package jpabook.jpashop.api.exceptionTest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ErrorResponse {
    private LocalDateTime timestamp = LocalDateTime.now();

    private String message; // 예외 메시지 저장

    private String code; // 예외를 세분화하기 위한 사용자 지정 코드

    private int status; // HTTP 상태 값 저장 400, 404, 500 등

    // @Valid 의 Parameter 검증을 통과하지 못한 필드가 담긴다.
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("errors")
    private List<MemberFieldError> memberFieldErrors;

    public ErrorResponse(){

    }
    static public ErrorResponse create(){
        return new ErrorResponse();
    }
    /*public ErrorResponse code(String code){
        this.code = code;
        return this;
    }
    public ErrorResponse status(int status){
        this.status = status;
        return this;
    }
    public ErrorResponse message(String message){
        this.message = message;
        return this;
    }*/
    public ErrorResponse errors(Errors errors){
        setMemberFieldErrors(errors.getFieldErrors());
        return this;
    }
    //BindingResult.getFieldErrors() 메소드를 통해 전달받은 fieldErrors
    private void setMemberFieldErrors(List<FieldError> fieldErrors) {
        memberFieldErrors = new ArrayList<MemberFieldError>();

        fieldErrors.forEach(error -> {
            memberFieldErrors.add(new MemberFieldError(
                    error.getCodes()[0],
                    error.getRejectedValue(),
                    error.getDefaultMessage()
            ));
        });
    }
    @Getter @AllArgsConstructor
    public static class MemberFieldError {
        private String field;
        private Object value;
        private String reason;

    }


}
