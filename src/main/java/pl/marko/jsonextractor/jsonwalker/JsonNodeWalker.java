package pl.marko.jsonextractor.jsonwalker;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public interface JsonNodeWalker {

    Stream<Object> walk(JsonNode node);

    Map<String,List<Object>> walkGrouped(JsonNode node);
}
