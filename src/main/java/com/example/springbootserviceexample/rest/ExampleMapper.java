package com.example.springbootserviceexample.rest;

import com.example.springbootserviceexample.data.Example;

public class ExampleMapper {

    public static ExampleDto toDto(Example example) {
        var exampleDto = new ExampleDto();
        exampleDto.setId(example.getId());
        exampleDto.setItem(example.getItem());

        return exampleDto;
    }

    public static Example toExample(ExampleDto exampleDto) {
        var example = new Example();
        example.setId(exampleDto.getId());
        example.setItem(exampleDto.getItem());
        return example;
    }
}
