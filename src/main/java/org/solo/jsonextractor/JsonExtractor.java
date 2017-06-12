package org.solo.jsonextractor;


import com.fasterxml.jackson.databind.JsonNode;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;

public class JsonExtractor {

    private final List<Pattern> keyPatterns;

    public JsonExtractor(String... keyPatterns) {
        this.keyPatterns = Stream.of(keyPatterns)
                .map(Pattern::compile)
                .collect(toList());
    }

    public Stream<Object> extract(JsonNode node) {
        return extract(node, false);
    }

    private Stream<Object> extract(JsonNode node, boolean propertyMatched) {
        if (node.isArray()) {
            return processArrayElement(node, (subnode, name) -> propertyMatched);
        }
        if (node.isObject()) {
            return processObjectElement(node);
        }
        return Stream.empty();
    }

    private boolean matchesPattern(String key) {
        return keyPatterns.stream()
                .anyMatch(pattern -> pattern.matcher(key).matches());
    }

    private Stream<Object> processObjectElement(JsonNode node) {
        return fieldsStream(node)
                .flatMap(field -> processSubnode(field.getValue(), field.getKey()));

    }

    private Stream<Map.Entry<String, JsonNode>> fieldsStream(JsonNode node) {
        Iterable<Map.Entry<String, JsonNode>> iterable = node::fields;
        return StreamSupport.stream(iterable.spliterator(), false);
    }

    private Stream<Object> processSubnode(JsonNode node, String nodeName) {
        if (node.isContainerNode()) {
            return extract(node, matchesPattern(nodeName));
        }
        if (matchesPattern(nodeName)) {
            return Stream.of(valueFrom(node));
        }
        return Stream.empty();
    }

    private Stream<Object> processArrayElement(JsonNode node, BiFunction<JsonNode, String, Boolean> canAddSubnode) {
        List<Object> result = new LinkedList<>();
        for (JsonNode subnode : node) {
            if (subnode.isContainerNode()) {
                result.addAll(
                        extract(subnode, false)
                                .collect(toList())
                );
            } else if (canAddSubnode.apply(subnode, null)) {
                result.add(valueFrom(subnode));
            }
        }
        return result.stream();
    }

    private Object valueFrom(JsonNode node) {
        switch (node.getNodeType()) {
            case STRING:
                return node.textValue();
            case NUMBER:
                return getNumber(node);
            case BOOLEAN:
                return node.asBoolean();
            case NULL:
            case ARRAY:
            case BINARY:
            case MISSING:
            case OBJECT:
            case POJO:
                return null;
        }
        return node.textValue();
    }

    private Object getNumber(JsonNode node) {
        switch (node.numberType()) {
            case BIG_DECIMAL:
                return node.decimalValue();
            case DOUBLE:
            case FLOAT:
                return node.asDouble();
            case LONG:
                return node.asLong();
            case BIG_INTEGER:
            case INT:
                return node.asInt();
        }
        return null;
    }
}


