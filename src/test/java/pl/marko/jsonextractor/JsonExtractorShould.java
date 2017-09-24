package pl.marko.jsonextractor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

abstract class JsonExtractorShould {
    JsonNode json(String content) throws IOException {
        return new ObjectMapper().readTree(content);
    }
}
