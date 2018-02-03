package pl.marko.jsonextractor.nodematcher;

public class AllFieldsMatcher  implements NodeMatcher {
    @Override
    public boolean matchesPattern(String nodeName) {
        return true;
    }
}
