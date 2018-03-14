package org.solo.jsonextractor.jsonwalker;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import org.solo.jsonextractor.nodematcher.NodeMatcher;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.Optional.empty;
import static java.util.Optional.of;

public class StreamJsonWalker implements JsonStreamWalker {

    private final NodeMatcher nodeMatcher;
    private final JsonFactory jfactory;

    public StreamJsonWalker(NodeMatcher nodeMatcher) {
        this.nodeMatcher = nodeMatcher;
        this.jfactory = new JsonFactory();
    }

    @Override
    public Stream<Object> walk(InputStream json) throws IOException {
        return streamJson(json)
                .map(Pair::right);
    }

    @Override
    public Map<String, List<Object>> walkGrouped(InputStream json) throws IOException {
        return streamJson(json)
                .collect(Pair.toMap());
    }

    private Stream<Pair> streamJson(InputStream json) throws IOException {
        LinkedList<Pair> result = new LinkedList<>();
        String fieldname = null;
        try (JsonParser jParser = jfactory.createParser(json)) {
            for (JsonToken jsonToken = jParser.nextToken(); jsonToken != null; jsonToken = jParser.nextToken()) {
                fieldname = fieldName(jParser, fieldname);
                getNodeValue(jParser, jsonToken, fieldname)
                        .ifPresent(result::add);
            }
        }
        return result.stream()
                .filter(this::nodeMatches);
    }

    private boolean nodeMatches(Pair pair) {
        return nodeMatcher.matchesPattern(pair.left());
    }

    private Optional<Pair> getNodeValue(JsonParser jParser, JsonToken jsonToken, String fieldname) throws IOException {
        switch (jsonToken) {
            case VALUE_FALSE:
            case VALUE_TRUE:
                return of(new Pair(fieldname, jParser.getBooleanValue()));
            case VALUE_STRING:
                return of(new Pair(fieldname, jParser.getText()));
            case VALUE_NUMBER_FLOAT:
                return of(new Pair(fieldname, jParser.getDecimalValue()));
            case VALUE_NUMBER_INT:
                return of(new Pair(fieldname, jParser.getIntValue()));
            case VALUE_NULL:
            case NOT_AVAILABLE:
            case START_OBJECT:
            case END_OBJECT:
            case START_ARRAY:
            case END_ARRAY:
            case FIELD_NAME:
            case VALUE_EMBEDDED_OBJECT:
                return empty();
        }
        return empty();
    }

    private String fieldName(JsonParser jParser, String currentFieldName) throws IOException {
        String currentName = jParser.getCurrentName();
        return currentName != null ? currentName : currentFieldName;
    }
}
