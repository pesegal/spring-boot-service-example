package com.example.springbootserviceexample.rest;

import com.example.springbootserviceexample.data.Example;
import com.example.springbootserviceexample.data.ExampleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class ExampleRestController {

    private final ExampleRepository exampleRepository;

    public ExampleRestController(ExampleRepository exampleRepository) {
        this.exampleRepository = exampleRepository;
    }

    @GetMapping("/hello")
    public String sayHello() {
        return "Hello World";
    }

    /***
     * Returns all example records...
     * @return ResponseEntity<Page<ExampleDto>>
     */
    @GetMapping("/example")
    public ResponseEntity<Page<ExampleDto>> getAllExamples(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy
    ) {
        var pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<ExampleDto> examples = exampleRepository.findAll(pageable).map(ExampleMapper::toDto);

        return ResponseEntity.ok(examples);
    }

    /***
     * Post always creates a new record in the database.
     * @param exampleDto if id is supplied. it's ignored.
     * @return ResponseEntity<Response<ExampleDto>>
     */
    @PostMapping("/example")
    public ResponseEntity<Response<ExampleDto>> createExample(@RequestBody ExampleDto exampleDto) {
        Response<ExampleDto> response = new Response<>();
        var example = new Example();
        example.setItem(exampleDto.getItem());
        response.setContent(ExampleMapper.toDto(exampleRepository.save(example)));
        return ResponseEntity.ok(response);
    }

    /***
     * Put is idempotent so it always returns the updated entity
     * @param exampleDto updates
     * @return ResponseEntity<Response<ExampleDto>>
     */
    @PutMapping("/example")
    public ResponseEntity<Response<ExampleDto>> updateExample(@RequestBody ExampleDto exampleDto) {
        Response<ExampleDto> response = new Response<>();
        var example = exampleRepository.findById(exampleDto.getId());
        if (example.isEmpty()) {
            return notFoundResponseMessage(exampleDto.getId());
        } else {
            var exampleEntity = example.get();
            exampleEntity.setItem(exampleDto.getItem());
            response.setContent(ExampleMapper.toDto(exampleRepository.save(exampleEntity)));
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/example/{id}")
    public ResponseEntity<Response<ExampleDto>> getExampleById(@PathVariable Long id) {
        Response<ExampleDto> response = new Response<>();
        var example = exampleRepository.findById(id);
        if (example.isEmpty()) {
            return notFoundResponseMessage(id);
        } else {
            response.setContent(ExampleMapper.toDto(example.get()));
            return ResponseEntity.ok(response);
        }
    }


    private ResponseEntity<Response<ExampleDto>> notFoundResponseMessage(long id) {
        var errorMessage = String.format("Example with id %d not found", id);
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, errorMessage);

        return ResponseEntity.of(problem).build();
    }
}
