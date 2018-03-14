package org.solo.jsonextractor;

import com.fasterxml.jackson.databind.JsonNode;
import org.solo.jsonextractor.jsonwalker.ComposedJsonWalker;
import org.solo.jsonextractor.jsonwalker.JsonWalker;
import org.solo.jsonextractor.jsonwalker.StreamJsonWalker;
import org.solo.jsonextractor.jsonwalker.TreeJsonWalker;
import org.solo.jsonextractor.nodematcher.AllFieldsMatcher;
import org.solo.jsonextractor.nodematcher.NamePatternMatcher;
import org.solo.jsonextractor.nodematcher.NodeMatcher;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class JsonExtractor {

    private final NodeMatcher nodeMatcher;
    private final JsonWalker treeWalker;

    private JsonExtractor(NodeMatcher nodeMatcher) {
        this.nodeMatcher = nodeMatcher;
        this.treeWalker = new TreeJsonWalker(nodeMatcher);
    }

    private JsonExtractor(NodeMatcher nodeMatcher, JsonWalker treeWalker) {
        this.nodeMatcher = nodeMatcher;
        this.treeWalker = treeWalker;
    }

    public static JsonExtractor byPattern(String... patterns) {
        return new JsonExtractor(new NamePatternMatcher(patterns));
    }

    public static JsonExtractor allFields() {
        return new JsonExtractor(new AllFieldsMatcher());
    }

    public Stream<Object> extract(JsonNode node) {
        return treeWalker.walk(node);
    }

    public Stream<Object> extract(InputStream json) throws IOException {
        return treeWalker.walk(json);
    }

    public JsonExtractor streamed() {
        ComposedJsonWalker composedJsonWalker = new ComposedJsonWalker(new StreamJsonWalker(this.nodeMatcher), this.treeWalker);
        return new JsonExtractor(this.nodeMatcher, composedJsonWalker);
    }

    public Map<String, List<Object>> extractGrouped(JsonNode node) {
        return treeWalker.walkGrouped(node);
    }

    public Map<String, List<Object>> extractGrouped(InputStream json) throws IOException {
        return treeWalker.walkGrouped(json);
    }
}
