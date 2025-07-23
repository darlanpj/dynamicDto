package com.example.dynamicDto.service;

import com.example.dynamicDto.helper.ToStringInterceptor;
import com.example.dynamicDto.model.JsonEntity;
import com.example.dynamicDto.repository.JsonEntityRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.MethodDelegation;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.Map;

@Service
public class DynamicDTOService {

    private final JsonEntityRepository repository;
    private final ObjectMapper mapper = new ObjectMapper();

    public DynamicDTOService(JsonEntityRepository repository) {
        this.repository = repository;
    }

    public void save(String id, JsonNode jsonNode) throws Exception {
     //   generateDTOPeloValor("DTO_" + id, jsonNode, new ByteBuddy());
     generateDTOPeloTipo("DTO_" + id, jsonNode, new ByteBuddy());
        String jsonString = mapper.writeValueAsString(jsonNode);
        repository.save(new JsonEntity(id, jsonString));
    }

    public String get(String id) {
        return repository.findById(id)
                .map(JsonEntity::getJson)
                .orElse(null);
    }

    public Class<?> generateDTOPeloValor(String className, JsonNode jsonNode, ByteBuddy buddy) throws Exception {
        DynamicType.Builder<Object> builder = buddy.subclass(Object.class).name("com.example.generated." + className);

        Iterator<Map.Entry<String, JsonNode>> fields = jsonNode.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> field = fields.next();
            String name = field.getKey();
            JsonNode value = field.getValue();

            Class<?> fieldType;
            if (value.isObject()) {
                fieldType = generateDTOPeloValor(className + capitalize(name), value, buddy);
            } else {
                fieldType = getJavaType(value);
            }

            builder = builder
                    .defineField(name, fieldType, Visibility.PRIVATE)
                    .defineMethod("get" + capitalize(name), fieldType, Visibility.PUBLIC)
                    .intercept(FieldAccessor.ofField(name))
                    .defineMethod("set" + capitalize(name), void.class, Visibility.PUBLIC)
                    .withParameters(fieldType)
                    .intercept(FieldAccessor.ofField(name));
        }

        return builder.make()
                .load(getClass().getClassLoader(), ClassLoadingStrategy.Default.WRAPPER)
                .getLoaded();
    }

    private static Class<?> getJavaType(JsonNode node) {
        if (node.isTextual()) return String.class;
        if (node.isInt()) return int.class;
        if (node.isLong()) return long.class;
        if (node.isDouble()) return double.class;
        if (node.isBoolean()) return boolean.class;
        return Object.class;
    }

    private static String capitalize(String s) {
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

//--------------------------------------------------------

    private Class<?> generateDTOPeloTipo(String className, JsonNode schemaNode, ByteBuddy byteBuddy) throws Exception {
        DynamicType.Builder<Object> builder = byteBuddy.subclass(Object.class).name("dto.generated." + className);

        Iterator<Map.Entry<String, JsonNode>> fields = schemaNode.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> entry = fields.next();
            String fieldName = entry.getKey();
            JsonNode typeOrObject = entry.getValue();

            Class<?> fieldType;
            if (typeOrObject.isObject()) {
                // tipo composto
                fieldType = generateDTOPeloTipo(className + "_" + capitalize(fieldName), typeOrObject, byteBuddy);
            } else if (typeOrObject.isTextual()) {
                fieldType = mapStringToType(typeOrObject.textValue());
            } else {
                throw new IllegalArgumentException("Tipo inválido para campo: " + fieldName);
            }

            builder = builder.defineField(fieldName, fieldType, Visibility.PRIVATE)
                    .defineMethod("get" + capitalize(fieldName), fieldType, Visibility.PUBLIC)
                    .intercept(FieldAccessor.ofField(fieldName))
                    .defineMethod("set" + capitalize(fieldName), void.class, Visibility.PUBLIC)
                    .withParameter(fieldType)
                    .intercept(FieldAccessor.ofField(fieldName));
        }

        builder = builder.defineMethod("toString", String.class, Visibility.PUBLIC)
                .intercept(MethodDelegation.to(ToStringInterceptor.class));

        return builder.make()
                .load(ClassLoader.getSystemClassLoader(), ClassLoadingStrategy.Default.WRAPPER)
                .getLoaded();
    }

    private Class<?> mapStringToType(String type) {
        return switch (type.toLowerCase()) {
            case "string" -> String.class;
            case "int", "integer" -> Integer.class;
            case "double" -> Double.class;
            case "boolean" -> Boolean.class;
            case "long" -> Long.class;
            case "float" -> Float.class;
            default -> throw new IllegalArgumentException("Tipo não suportado: " + type);
        };
    }
}
