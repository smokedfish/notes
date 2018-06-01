package org.rob.notes.responsebuilders;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.http.MediaType;

public class MarkDownResponseBuilder implements ResponseBuilder {
    private final Parser parser = Parser.builder().build();
    private final HtmlRenderer renderer = HtmlRenderer.builder().build();

    @Override
    public byte[] response(File doc) throws Exception {
        try (InputStreamReader input = new InputStreamReader(new FileInputStream(doc), StandardCharsets.UTF_8)) {
            Node document = parser.parseReader(input);
            StringBuilder sb = new StringBuilder()
            .append("<!DOCTYPE html>")
            .append("<html>")
            .append("<body>");
            renderer.render(document, sb);
            sb.append("</body>")
            .append("</html>");
            return sb.toString().getBytes(StandardCharsets.UTF_8);
        }
    }

    @Override
    public MediaType getMediaType() {
        return MediaType.TEXT_HTML;
    }
}
