package jpabook.jpashop.api.exceptionTest;

public class MemberException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    private ErrorCode errorCode;

    public ErrorCode getErrorCode() {
        return errorCode;
    }

   public MemberException(ErrorCode errorCode){
       super(errorCode.getMessage());
       this.errorCode = errorCode;
   }
}
