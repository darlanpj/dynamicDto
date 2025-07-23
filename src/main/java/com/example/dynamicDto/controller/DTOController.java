package com.example.dynamicDto.controller;

import com.example.dynamicDto.service.DynamicDTOService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/dto")
public class DTOController {

    private final DynamicDTOService service;

    public DTOController(DynamicDTOService service) {
        this.service = service;
    }

    @PostMapping("/{id}")
    public ResponseEntity<String> create(@PathVariable String id,
                                         @RequestBody @Schema(hidden = true)JsonNode jsonNode) {
        try {
            service.save(id, jsonNode);
            return ResponseEntity.ok("DTO gerado e salvo no H2 com ID: " + id);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<JsonNode> get(@PathVariable String id) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String json = service.get(id);
            JsonNode jsonNode = objectMapper.readTree(json);
            return ResponseEntity.ok(jsonNode);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

