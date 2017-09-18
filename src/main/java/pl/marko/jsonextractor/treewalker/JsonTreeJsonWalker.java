package pl.marko.jsonextractor.treewalker;


import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;
import static java.util.stream.Stream.empty;
import static java.util.stream.StreamSupport.stream;

public class JsonTreeJsonWalker {

    private final NodeValueExtractor nodeValueExtractor;
    private final NodeMatcher nodeMatcher;

    public JsonTreeJsonWalker(NodeMatcher patternMatcher, NodeValueExtractor nodeValueExtractor) {
        this.nodeValueExtractor = nodeValueExtractor;
        this.nodeMatcher = patternMatcher;
    }

    public Stream<Object> walk(JsonNode node) {
        return walk(node, null)
                .map(Pair::right);
    }

    private Stream<Pair> walk(JsonNode node, String propertyName) {
        if (node.isArray()) {
            return walkArray(node, propertyName);
        }
        if (node.isObject()) {
            return walkObject(node);
        }
        if (propertyMatches(propertyName)) {
            return getSimpleValue(node, propertyName);
        }
        return empty();
    }

    private Stream<Pair> getSimpleValue(JsonNode node, String propertyName) {
        return nodeValueExtractor.valueFrom(node)
                .map(value -> pair(propertyName, value));
    }

    private boolean propertyMatches(String propertyName) {
        return propertyName!=null && nodeMatcher.matchesPattern(propertyName);
    }

    private Stream<Pair> walkArray(JsonNode node, String propertyName) {
        return elementsStream(node)
                .flatMap(el -> walk(el,propertyName));
    }

    private Stream<Pair> walkObject(JsonNode node) {
        return fieldsStream(node)
                .flatMap(field -> walk(field.getValue(), field.getKey()));

    }

    private Stream<JsonNode> elementsStream(JsonNode node) {
        return stream(node.spliterator(), false);
    }

    private Stream<Map.Entry<String, JsonNode>> fieldsStream(JsonNode node) {
        Iterable<Map.Entry<String, JsonNode>> iterable = node::fields;
        return stream(iterable.spliterator(), false);
    }

    public Map<String,List<Object>> walkGrouped(JsonNode node) {
        return walk(node, null)
                .collect(toMap());

    }

    private Collector<Pair, ?, Map<String, List<Object>>> toMap() {
        return groupingBy(Pair::left, mapping(Pair::right, toList()));
    }

    private Pair pair(String left, Object right){
        return new Pair(left, right);
    }

    private class Pair {
        private String left;

        private Object right;

        private Pair(String left, Object right) {
            this.left = left;
            this.right = right;
        }

        private String left() {
            return left;
        }
        private Object right() {
            return right;
        }
    }
}


