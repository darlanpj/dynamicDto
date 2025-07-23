package com.example.dynamicDto.repository;

import com.example.dynamicDto.model.JsonEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JsonEntityRepository extends JpaRepository<JsonEntity, String> {
}
