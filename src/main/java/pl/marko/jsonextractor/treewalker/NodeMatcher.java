package pl.marko.jsonextractor.treewalker;

public interface NodeMatcher {

    boolean matchesPattern(String nodeName);
}
