package pl.marko.jsonextractor.benchmark;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import pl.marko.jsonextractor.JsonExtractor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.empty;
import static java.util.stream.Stream.of;


public class ExtractorBenchmark {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static List<JsonNode> readNodes(String resourcePath) throws IOException {

        try (InputStream resource = ExtractorBenchmark.class.getResourceAsStream(resourcePath)) {
            Stream<String> lines = new BufferedReader(new InputStreamReader(resource, StandardCharsets.UTF_8)).lines();
            return lines.flatMap(ExtractorBenchmark::readJson)
                    .collect(toList());
        }
    }

    private static Stream<JsonNode> readJson(String line) {
        try {
            return of(objectMapper.readTree(line));
        } catch (IOException e) {
            return empty();
        }
    }

    @State(Scope.Thread)
    public static class MyState {
        List<JsonNode> nodes;
        JsonExtractor extractor;

        @Setup(Level.Trial)
        public void doSetup() throws IOException {
            nodes = readNodes("/payload.json");
            extractor = JsonExtractor.byPattern(".*Id.*", ".*Name.*");
        }
    }


    @Benchmark @BenchmarkMode(Mode.AverageTime) @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public void testMethod(MyState state, Blackhole blackhole) {
        for(JsonNode node : state.nodes){
            List<Object> collect = state.extractor.extract(node).collect(toList());
            blackhole.consume(collect);
        }
    }

}


