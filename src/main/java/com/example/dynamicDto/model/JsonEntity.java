package com.example.dynamicDto.model;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;

@Entity
public class JsonEntity {

    @Id
    private String id;

    @Lob
    private String json;

    public JsonEntity() {}

    public JsonEntity(String id, String json) {
        this.id = id;
        this.json = json;
    }

    public String getId() {
        return id;
    }

    public String getJson() {
        return json;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setJson(String json) {
        this.json = json;
    }
}
