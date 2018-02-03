package pl.marko.jsonextractor.nodematcher;

import java.util.List;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.of;

public class NamePatternMatcher implements NodeMatcher {
    private final List<Pattern> keyPatterns;

    public NamePatternMatcher(String... keyPatterns) {
        this.keyPatterns = of(keyPatterns)
                .map(Pattern::compile)
                .collect(toList());
    }

    @Override
    public boolean matchesPattern(String nodeName) {
        return keyPatterns.stream()
                .anyMatch(pattern -> pattern.matcher(nodeName).matches());
    }
}
