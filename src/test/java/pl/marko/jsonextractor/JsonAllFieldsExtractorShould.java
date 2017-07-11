package pl.marko.jsonextractor;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Test;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class JsonAllFieldsExtractorShould extends JsonExtractorShould {

    @Test public void
    extractSimpleValueFromJson() throws Exception {
        JsonNode jsonNode = json("{\"a\": 1, \"b\":2}");

        Stream<Object> values = JsonExtractor.allFields().extract(jsonNode);

        assertThat(values).containsExactly(1, 2);
    }

    @Test public void
    extractValuesFromNotTrivialJson() throws Exception {
        JsonNode jsonNode = json("{\"1111a\": 1, \"b\":2, \"child\": {\"a\":3, \"x\":4, \"anotherChild\":{\"c\":\"ala\", " +
                "\"2222a\":\"ma\"}}}");

        Stream<Object> values = JsonExtractor.allFields().extract(jsonNode);

        assertThat(values).containsExactly(1, 2, 3, 4, "ala", "ma");
    }
}
