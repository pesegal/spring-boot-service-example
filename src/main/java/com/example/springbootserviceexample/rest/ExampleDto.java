package com.example.springbootserviceexample.rest;

public class ExampleDto   {
    private Long id;
    private String item;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    @Override
    public String toString() {
        return "ExampleDto{" +
                "id=" + id +
                ", item='" + item + '\'' +
                '}';
    }
}
