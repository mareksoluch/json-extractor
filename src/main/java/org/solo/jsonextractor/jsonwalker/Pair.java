package org.solo.jsonextractor.jsonwalker;

import java.util.List;
import java.util.Map;
import java.util.stream.Collector;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

class Pair {
    private String left;

    private Object right;

    Pair(String left, Object right) {
        this.left = left;
        this.right = right;
    }

    String left() {
        return left;
    }
    Object right() {
        return right;
    }

    static Collector<Pair, ?, Map<String, List<Object>>> toMap() {
        return groupingBy(Pair::left, mapping(Pair::right, toList()));
    }
}
