package pl.marko.jsonextractor.jsonwalker;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import pl.marko.jsonextractor.nodematcher.NodeMatcher;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Stream.empty;
import static java.util.stream.StreamSupport.stream;
import static pl.marko.jsonextractor.jsonwalker.Pair.toMap;

public class TreeJsonWalker implements JsonWalker {

    private final SimpleValueExtractor nodeValueExtractor = new SimpleValueExtractor();
    private final NodeMatcher nodeMatcher;

    public TreeJsonWalker(NodeMatcher patternMatcher) {
        this.nodeMatcher = patternMatcher;
    }

    @Override
    public Stream<Object> walk(InputStream json) throws IOException {
        return walk(jsonNode(json));
    }

    @Override
    public Map<String,List<Object>> walkGrouped(InputStream json) throws IOException {
        return walkGrouped(jsonNode(json));
    }

    @Override
    public Stream<Object> walk(JsonNode node) {
        return walk(node, null)
                .map(Pair::right);
    }

    @Override
    public Map<String,List<Object>> walkGrouped(JsonNode node) {
        return walk(node, null)
                .collect(toMap());

    }

    private JsonNode jsonNode(InputStream json) throws IOException {
        return new ObjectMapper().readTree(json);
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


    private Pair pair(String left, Object right){
        return new Pair(left, right);
    }
}


