package com.example.testassignment.data;

public class Response {
    private Object data;

    public Response(Object data){
        this.data = data;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
