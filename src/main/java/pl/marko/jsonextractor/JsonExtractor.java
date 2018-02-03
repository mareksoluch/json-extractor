package pl.marko.jsonextractor;

import com.fasterxml.jackson.databind.JsonNode;
import pl.marko.jsonextractor.jsonwalker.ComposedJsonWalker;
import pl.marko.jsonextractor.jsonwalker.JsonWalker;
import pl.marko.jsonextractor.jsonwalker.StreamJsonWalker;
import pl.marko.jsonextractor.jsonwalker.TreeJsonWalker;
import pl.marko.jsonextractor.nodematcher.NodeMatcher;
import pl.marko.jsonextractor.nodematcher.AllFieldsMatcher;
import pl.marko.jsonextractor.nodematcher.NamePatternMatcher;

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
