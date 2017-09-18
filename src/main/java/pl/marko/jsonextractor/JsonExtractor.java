package pl.marko.jsonextractor;

import com.fasterxml.jackson.databind.JsonNode;
import pl.marko.jsonextractor.treewalker.JsonTreeJsonWalker;
import pl.marko.jsonextractor.treewalker.NodeMatcher;
import pl.marko.jsonextractor.treewalker.NodeValueExtractor;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class JsonExtractor {

    private final JsonTreeJsonWalker treeWalker;

    private JsonExtractor(NodeMatcher nodeMatcher, NodeValueExtractor nodeValueExtractor) {
        treeWalker = new JsonTreeJsonWalker(nodeMatcher, nodeValueExtractor);
    }

    public static JsonExtractor byPattern(String... patterns){
        return new JsonExtractor(new NamePatternMatcher(patterns), new SimpleValueExtractor());
    }

    public Stream<Object> extract(JsonNode node){
        return treeWalker.walk(node);
    }

    public static JsonExtractor allFields() {
        return new JsonExtractor(new AllFieldsMatcher(), new SimpleValueExtractor());
    }

    public Map<String, List<Object>> extractGrouped(JsonNode node) {
        return treeWalker.walkGrouped(node);
    }
}
