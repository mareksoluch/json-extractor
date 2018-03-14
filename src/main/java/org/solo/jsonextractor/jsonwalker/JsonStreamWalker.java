package org.solo.jsonextractor.jsonwalker;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public interface JsonStreamWalker {
    Stream<Object> walk(InputStream json) throws IOException;
    Map<String,List<Object>> walkGrouped(InputStream json) throws IOException;
}
