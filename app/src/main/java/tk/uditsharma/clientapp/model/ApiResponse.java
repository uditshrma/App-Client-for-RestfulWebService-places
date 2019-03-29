package tk.uditsharma.clientapp.model;

public class ApiResponse<T> {
    private T data;
    private Throwable error;
    private int code;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Throwable getError() {
        return error;
    }

    public void setError(Throwable error) {
        this.error = error;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public ApiResponse(T data) {
        this.data = data;
        this.error = null;
    }

    public ApiResponse(T data, int code) {
        this.data = data;
        this.code = code;
        this.error = null;
    }

    public ApiResponse(Throwable error) {
        this.error = error;
        this.data = null;
    }

    public ApiResponse(int code) {
        this.code = code;
        this.error = null;
        this.data = null;
    }

    public ApiResponse() {
        this.data = null;
        this.error = null;
    }
}
