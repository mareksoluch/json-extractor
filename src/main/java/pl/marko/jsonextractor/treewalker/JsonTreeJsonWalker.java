package pl.marko.jsonextractor.treewalker;


import com.fasterxml.jackson.databind.JsonNode;

import java.util.Map;
import java.util.stream.Stream;

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
        return walk(node, false);
    }

    private Stream<Object> walk(JsonNode node, boolean nodeMatches) {
        if (node.isArray()) {
            return walkArray(node, nodeMatches);
        }
        if (node.isObject()) {
            return walkObject(node);
        }
        if (nodeMatches) {
            return nodeValueExtractor.valueFrom(node);
        }
        return empty();
    }

    private Stream<Object> walkArray(JsonNode node, boolean propertyMatched) {
        return elementsStream(node)
                .flatMap(el -> walk(el,propertyMatched));
    }

    private Stream<Object> walkObject(JsonNode node) {
        return fieldsStream(node)
                .flatMap(field -> walk(field.getValue(), nodeMatcher.matchesPattern(field.getValue(), field.getKey())));

    }

    private Stream<JsonNode> elementsStream(JsonNode node) {
        return stream(node.spliterator(), false);
    }

    private Stream<Map.Entry<String, JsonNode>> fieldsStream(JsonNode node) {
        Iterable<Map.Entry<String, JsonNode>> iterable = node::fields;
        return stream(iterable.spliterator(), false);
    }

}


