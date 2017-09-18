package pl.marko.jsonextractor;

import pl.marko.jsonextractor.treewalker.NodeMatcher;

public class AllFieldsMatcher  implements NodeMatcher {
    @Override
    public boolean matchesPattern(String nodeName) {
        return true;
    }
}
