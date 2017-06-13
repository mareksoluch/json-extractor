package pl.marko.jsonextractor.treewalker;

import com.fasterxml.jackson.databind.JsonNode;

public interface NodeMatcher {

    boolean matchesPattern(JsonNode node, String nodeName);
}
