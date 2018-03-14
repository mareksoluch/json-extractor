package org.solo.jsonextractor;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class JsonAllFieldsExtractorShould extends JsonExtractorShould {

    private String name;
    private String json;
    private Object[] result;

    public JsonAllFieldsExtractorShould(String name, String json, Object[] result) {
        this.name = name;
        this.json = json;
        this.result = result;
    }

    @Parameters(name = "{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(
                test("extractSimpleValue",
                        "{\"a\": 1, \"b\":2}",
                        result(1, 2)),
                test("extractValuesFromNotTrivialJson",
                        "{\"1111a\": 1, \"b\":2, " +
                                "\"child\": {\"a\":3, \"x\":4, " +
                                "\"anotherChild\":{\"c\":\"ala\", \"2222a\":\"ma\"}}}",
                        result(1, 2, 3, 4, "ala", "ma")));
    }


    private static Object[] test(String name, String json, Object[] result) {
        return new Object[]{name, json, result};
    }

    private static Object[] result(Object... result) {
        return result;
    }

    @Test
    public void
    extractFieldsFromJson() throws Exception {
        JsonNode jsonNode = json(json);

        Stream<Object> values = JsonExtractor.allFields().extract(jsonNode);

        assertThat(values).containsExactly(result);
    }

    @Test
    public void
    extractFieldsFromStream() throws Exception {
        InputStream jsonNode = jsonStream(json);

        Stream<Object> values = JsonExtractor.allFields().streamed().extract(jsonNode);

        assertThat(values).containsExactly(result);
    }

}