package pl.marko.jsonextractor;

import com.fasterxml.jackson.databind.JsonNode;
import pl.marko.jsonextractor.treewalker.NodeMatcher;

public class AllFieldsMatcher  implements NodeMatcher {
    @Override
    public boolean matchesPattern(JsonNode node, String nodeName) {
        return true;
    }
}
