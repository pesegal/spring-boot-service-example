package com.example.springbootserviceexample.rest;

public class Response<R> {
    private R content;

    public R getContent() {
        return content;
    }

    public void setContent(R content) {
        this.content = content;
    }
}
