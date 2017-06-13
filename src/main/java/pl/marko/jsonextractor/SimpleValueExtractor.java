package pl.marko.jsonextractor;

import com.fasterxml.jackson.databind.JsonNode;
import pl.marko.jsonextractor.treewalker.NodeValueExtractor;

import java.util.stream.Stream;

class SimpleValueExtractor implements NodeValueExtractor {
    Stream<Object> getNumber(JsonNode node) {
        switch (node.numberType()) {
            case BIG_DECIMAL:
                return Stream.of(node.decimalValue());
            case DOUBLE:
            case FLOAT:
                return Stream.of(node.asDouble());
            case LONG:
                return Stream.of(node.asLong());
            case BIG_INTEGER:
            case INT:
                return Stream.of(node.asInt());
        }
        return Stream.empty();
    }

    @Override
    public Stream<Object> valueFrom(JsonNode node) {
        switch (node.getNodeType()) {
            case STRING:
                return Stream.of(node.textValue());
            case NUMBER:
                return getNumber(node);
            case BOOLEAN:
                return Stream.of(node.asBoolean());
            case NULL:
            case ARRAY:
            case BINARY:
            case MISSING:
            case OBJECT:
            case POJO:
                return Stream.empty();
        }
        return Stream.of(node.textValue());
    }
}
