package org.solo.jsonextractor;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import static org.assertj.core.api.Assertions.assertThat;

public class JsonPatternGroupedExtractorShould extends JsonExtractorShould {

    @Test public void
    extractFieldsFromJson() throws Exception {
        testFieldsExtractionToFieldsGroup(this::fromJson);
    }

    @Test public void
    extractFieldsFromJsonStream() throws Exception {
        testFieldsExtractionToFieldsGroup(this::fromStream);
    }

    private void testFieldsExtractionToFieldsGroup(BiFunction<String, String[], Map<String, List<Object>>> extractValuesFromJson) throws IOException {
        String json = "{\"array\": [{\"a\":1,\"b\":2},{\"x\":3,\"a\":4}], \"bob\": 5}";

        Map<String, List<Object>> values = fromJson(json, "a", "b.*");

        assertThat(values.keySet()).containsExactly("a", "b", "bob");
        assertThat(values.get("a")).containsExactly(1, 4);
        assertThat(values.get("b")).containsExactly(2);
        assertThat(values.get("bob")).containsExactly(5);
    }

    private Map<String, List<Object>> fromJson(String json, String ... pattern) {
        try {
            JsonNode jsonNode = json(json);
            return extractor(pattern).extractGrouped(jsonNode);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, List<Object>> fromStream(String json, String ... pattern) {
        try {
            InputStream jsonNode = jsonStream(json);
            return extractor(pattern).streamed().extractGrouped(jsonNode);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    protected JsonExtractor extractor(String... pattern) {
        return JsonExtractor.byPattern(pattern);
    }
}