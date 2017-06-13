package pl.marko.jsonextractor.treewalker;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.stream.Stream;

public interface NodeValueExtractor {
    Stream<Object> valueFrom(JsonNode node);
}
