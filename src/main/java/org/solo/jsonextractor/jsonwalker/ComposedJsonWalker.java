package org.solo.jsonextractor.jsonwalker;

import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class ComposedJsonWalker implements JsonWalker {

    private final JsonNodeWalker jsonNodeWalker;
    private final JsonStreamWalker jsonStreamWalker;

    public ComposedJsonWalker(JsonStreamWalker jsonStreamWalker, JsonNodeWalker jsonNodeWalker) {
        this.jsonStreamWalker = jsonStreamWalker;
        this.jsonNodeWalker = jsonNodeWalker;
    }

    @Override
    public Stream<Object> walk(InputStream json) throws IOException {
        return jsonStreamWalker.walk(json);
    }

    @Override
    public Map<String, List<Object>> walkGrouped(InputStream json) throws IOException {
        return jsonStreamWalker.walkGrouped(json);
    }

    @Override
    public Stream<Object> walk(JsonNode node) {
        return jsonNodeWalker.walk(node);
    }

    @Override
    public Map<String, List<Object>> walkGrouped(JsonNode node) {
        return jsonNodeWalker.walkGrouped(node);
    }


}
