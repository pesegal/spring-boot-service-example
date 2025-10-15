package com.example.springbootserviceexample.rest;


import com.example.springbootserviceexample.data.ExampleRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ExampleRestControllerTest {
    Logger logger = LoggerFactory.getLogger(ExampleRestControllerTest.class);


    @Autowired
    private static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest");

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ExampleRepository exampleRepository;



    @Autowired
    ExampleRestController restController;

    @DynamicPropertySource
    static void postgreSQLProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @BeforeAll
    static void setUp() {
        postgres.start();
    }

    @BeforeEach
    void clearDatabase() {
        exampleRepository.deleteAll();
    }

    @AfterAll
    static void tearDown() {
        postgres.stop();
    }

    @Test
    void test() {
        assertTrue(true);
    }

    @Test
    void hello() {
        ResponseEntity<String> response = restTemplate.getForEntity("/api/v1/hello", String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Hello World", response.getBody());
    }

    @Test
    void testExamplePost() {
        var post = new ExampleDto();
        post.setItem("test_name");
        HttpEntity<ExampleDto> request = new HttpEntity<>(post);
        ResponseEntity<Response<ExampleDto>> response = restTemplate.exchange(
                "/api/v1/example",
                HttpMethod.POST, request, 
                new ParameterizedTypeReference<Response<ExampleDto>>() {}
        );
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("test_name", response.getBody().getContent().getItem());
    }
    
    @Test
    void test_ReturnMultipleItems() {
        for (int i = 0; i < 4; i++) {
            var post = new ExampleDto();
            post.setItem("test_name " + i);
            HttpEntity<ExampleDto> request = new HttpEntity<>(post);
            ResponseEntity<Response<ExampleDto>> response = restTemplate.exchange(
                    "/api/v1/example",
                    HttpMethod.POST, request,
                    new ParameterizedTypeReference<Response<ExampleDto>>() {}
            );            
        }
        // Verify that 3 records were created.
        var response = restTemplate.exchange(
                "/api/v1/example",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Response<List<ExampleDto>>>() {}
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        logger.info(() -> response.getBody().toString());
        assertEquals(4, response.getBody().getTotalElements());
    }


    @Test
    void test_NoItemsReturned() {
        var response = restTemplate.exchange(
                "/api/v1/example",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Response<List<ExampleDto>>>() {}
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, response.getBody().getTotalElements());
    }

}
