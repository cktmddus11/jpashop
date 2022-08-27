package jpabook.jpashop.api.exceptionTest;



public enum ErrorCode {
    INVALID_PARAMETER(400, null, "Invalid Request Data");
    private final String code;
    private final String message;
    private final int status;

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }

    ErrorCode(final int status, final String code, final String memssage) {
        this.message = memssage;
        this.status = status;
        this.code = code;
    }
}
