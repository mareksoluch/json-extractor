package org.solo.jsonextractor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.util.stream.Stream;

public class JsonExtractorShould {

    @Test
    public void extractSimpleValueFromJson() throws Exception {
        JsonNode jsonNode = new ObjectMapper().readTree("{\"a\": 1, \"b\":2}");

        Stream<Object> values = new JsonExtractor("a").extract(jsonNode);

        Assertions.assertThat(values).containsExactly(1);
    }

    @Test
    public void extractSimpleValueFromJson2() throws Exception {
        JsonNode jsonNode = new ObjectMapper().readTree("{\"a\": 1, \"b\":2, \"child\": {\"a\":3, \"x\":4, \"anotherChild\":{\"c\":\"ala\", \"a\":\"ma\"}}}");

        Stream<Object> values = new JsonExtractor("a").extract(jsonNode);

        Assertions.assertThat(values).containsExactly(1, 3, "ma");
    }

    @Test
    public void shouldPatternMatch() throws Exception {
        JsonNode jsonNode = new ObjectMapper().readTree("{\"1111a\": 1, \"b\":2, \"child\": {\"a\":3, \"x\":4, \"anotherChild\":{\"c\":\"ala\", " +
                "\"2222a\":\"ma\"}}}");

        Stream<Object> values = new JsonExtractor(".*a").extract(jsonNode);

        Assertions.assertThat(values).containsExactly(1, 3, "ma");
    }

    @Test
    public void shouldWorkWithArrays() throws Exception {
        JsonNode jsonNode = new ObjectMapper().readTree("{\"array\": [{\"a\":1,\"b\":2},{\"x\":1,\"a\":3}]}");

        Stream<Object> values = new JsonExtractor("a").extract(jsonNode);

        Assertions.assertThat(values).containsExactly(1,3);
    }

    @Test
    public void shouldWorkWithArraysOfSimpleValues() throws Exception {
        JsonNode jsonNode = new ObjectMapper().readTree("{\"a\": [1,2,3,4]}");

        Stream<Object> values = new JsonExtractor("a").extract(jsonNode);

        Assertions.assertThat(values).containsExactly(1,2,3,4);
    }

}