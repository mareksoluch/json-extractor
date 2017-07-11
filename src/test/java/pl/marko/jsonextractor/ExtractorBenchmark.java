package pl.marko.jsonextractor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.empty;
import static java.util.stream.Stream.of;


public class ExtractorBenchmark {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static List<JsonNode> readNodes(String jsonFile) throws IOException {
        try (Stream<String> stream = Files.lines(Paths.get(jsonFile))) {
            return stream.flatMap(ExtractorBenchmark::readJson)
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
            nodes = readNodes(MyState.class.getResource("/payload.json").getFile());
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


