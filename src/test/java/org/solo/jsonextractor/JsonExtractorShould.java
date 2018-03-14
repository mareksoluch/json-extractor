package org.solo.jsonextractor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

abstract class JsonExtractorShould {
    JsonNode json(String content) throws IOException {
        return new ObjectMapper().readTree(content);
    }

    InputStream jsonStream(String content) throws IOException {
        return new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8.name()));
    }
}
