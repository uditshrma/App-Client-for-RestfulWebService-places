package tk.uditsharma.clientapp.model;

public class ApiResponse<T> {
    private T data;
    private Throwable error;

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

    public ApiResponse(T data) {
        this.data = data;
        this.error = null;
    }

    public ApiResponse(Throwable error) {
        this.error = error;
        this.data = null;
    }
}
