package com.example.springbootserviceexample.data;


import org.springframework.data.jpa.repository.JpaRepository;


public interface ExampleRepository extends JpaRepository<Example, Long> {
}
