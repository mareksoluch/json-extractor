package pl.marko.jsonextractor;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class JsonPatternExtractorShould extends JsonExtractorShould {

    @Test public void
    extractSimpleValueFromJson() throws Exception {
        JsonNode jsonNode = json("{\"a\": 1, \"b\":2}");

        Stream<Object> values = JsonExtractor.byPattern("a").extract(jsonNode);

        assertThat(values).containsExactly(1);
    }

    @Test public void
    extractSimpleValueFromComplexJson() throws Exception {
        JsonNode jsonNode = json("{\"a\": 1, \"b\":2, \"child\": {\"a\":3, \"x\":4, \"anotherChild\":{\"c\":\"ala\", \"a\":\"ma\"}}}");

        Stream<Object> values = JsonExtractor.byPattern("a").extract(jsonNode);

        assertThat(values).containsExactly(1, 3, "ma");
    }

    @Test public void
    shouldMatchNotTrivialPattern() throws Exception {
        JsonNode jsonNode = json("{\"1111a\": 1, \"b\":2, \"child\": {\"a\":3, \"x\":4, \"anotherChild\":{\"c\":\"ala\", " +
                "\"2222a\":\"ma\"}}}");

        Stream<Object> values = JsonExtractor.byPattern(".*a").extract(jsonNode);

        assertThat(values).containsExactly(1, 3, "ma");
    }

    @Test public void
    workWithArrays() throws Exception {
        JsonNode jsonNode = json("{\"array\": [{\"a\":1,\"b\":2},{\"x\":1,\"a\":3}]}");

        Stream<Object> values = JsonExtractor.byPattern("a").extract(jsonNode);

        assertThat(values).containsExactly(1, 3);
    }

    @Test public void
    workWithArraysOfSimpleValues() throws Exception {
        JsonNode jsonNode = json("{\"a\": [1,2,3,4]}");

        Stream<Object> values = JsonExtractor.byPattern("a").extract(jsonNode);

        assertThat(values).containsExactly(1, 2, 3, 4);
    }

    @Test public void
    processMoreThanOnePattern() throws Exception {
        JsonNode jsonNode = json("{\"array\": [{\"a\":1,\"b\":2},{\"x\":3,\"a\":4}], \"bob\": 5}");

        Stream<Object> values = JsonExtractor.byPattern("a", "b.*").extract(jsonNode);

        assertThat(values).containsExactly(1, 2, 4, 5);
    }

    @Test public void
    extractGroupedFields() throws Exception {
        JsonNode jsonNode = json("{\"array\": [{\"a\":1,\"b\":2},{\"x\":3,\"a\":4}], \"bob\": 5}");

        Map<String, List<Object>> values = JsonExtractor.byPattern("a", "b.*").extractGrouped(jsonNode);

        assertThat(values.keySet()).containsExactly("a", "b", "bob");
        assertThat(values.get("a")).containsExactly(1, 4);
        assertThat(values.get("b")).containsExactly(2);
        assertThat(values.get("bob")).containsExactly(5);
    }

}