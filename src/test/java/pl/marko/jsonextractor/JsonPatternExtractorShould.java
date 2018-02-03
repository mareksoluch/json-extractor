package pl.marko.jsonextractor;

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
public class JsonPatternExtractorShould extends JsonExtractorShould {

    private String name;
    private String json;
    private String[] pattern;
    private Object[] result;

    public JsonPatternExtractorShould(String name,String json, String[] pattern, Object[] result) {
        this.name = name;
        this.json = json;
        this.pattern = pattern;
        this.result = result;
    }

    @Parameters( name = "{0}" )
    public static Collection<Object[]> data() {
        return Arrays.asList(
                test("extractSimpleValueFromJson",
                        "{\"a\": 1, \"b\":2}",
                        pattern("a"),
                        result(1)),
                test("extractSimpleValueFromComplexJson",
                        "{\"a\": 1, \"b\":2, \"child\": {\"a\":3, \"x\":4, \"anotherChild\":{\"c\":\"ala\", \"a\":\"ma\"}}}",
                        pattern("a"),
                        result(1, 3, "ma")),
                test("shouldMatchNotTrivialPattern",
                        "{\"1111a\": 1, \"b\":2, \"child\": {\"a\":3, \"x\":4, \"anotherChild\":{\"c\":\"ala\", \"2222a\":\"ma\"}}}",
                        pattern(".*a"),
                        result(1, 3, "ma")),
                test("workWithArrays",
                        "{\"array\": [{\"a\":1,\"b\":2},{\"x\":1,\"a\":3}]}",
                        pattern("a"),
                        result(1, 3)),
                test("workWithArraysOfSimpleValues",
                        "{\"a\": [1,2,3,4]}",
                        pattern("a"),
                        result(1, 2, 3, 4)),
                test("processMoreThanOnePattern",
                        "{\"array\": [{\"a\":1,\"b\":2},{\"x\":3,\"a\":4}], \"bob\": 5}",
                        pattern("a", "b.*"),
                        result(1, 2, 4, 5)));
    }

    private static Object[] test(String name,String json, String[] pattern, Object[] result) {
        return new Object[]{name, json, pattern, result};
    }

    private static Object[] result(Object... result) {
        return result;
    }

    private static String[] pattern(String... a) {
        return a;
    }

    @Test public void
    extractFieldsFromJson() throws Exception {
        JsonNode jsonNode = json(json);

        Stream<Object> values = extractor(pattern).extract(jsonNode);

        assertThat(values).containsExactly(result);
    }

    @Test public void
    extractFieldsFromStream() throws Exception {
        InputStream jsonNode = jsonStream(json);

        Stream<Object> values = extractor(pattern).streamed().extract(jsonNode);

        assertThat(values).containsExactly(result);
    }

    protected JsonExtractor extractor(String... pattern) {
        return JsonExtractor.byPattern(pattern);
    }
}