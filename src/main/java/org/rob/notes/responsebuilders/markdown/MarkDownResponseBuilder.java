package org.rob.notes.responsebuilders.markdown;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import org.commonmark.Extension;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.ext.heading.anchor.HeadingAnchorExtension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.rob.notes.responsebuilders.FileResponseBuilder;
import org.springframework.http.MediaType;

public class MarkDownResponseBuilder implements FileResponseBuilder {
    private final Parser parser;
    private final HtmlRenderer renderer;

    public MarkDownResponseBuilder() {
        List<Extension> extensions = Arrays.asList(
                TablesExtension.create(),
                HeadingAnchorExtension.create());

        this.parser = Parser.builder()
                .extensions(extensions)
                .build();

        this.renderer = HtmlRenderer.builder()
                .extensions(extensions)
                .build();
    }

    @Override
    public byte[] response(File doc) throws Exception {
        try (InputStreamReader input = new InputStreamReader(new FileInputStream(doc), StandardCharsets.UTF_8)) {
            Node document = parser.parseReader(input);
            ContentsBuilder contentsBuilder = new ContentsBuilder();
            document.accept(contentsBuilder);
            String eol = System.lineSeparator();
            StringBuilder sb = new StringBuilder()
            .append("<!doctype html>").append(eol)
            .append("<html>").append(eol)
            .append("<head>").append(eol)
            .append("<link rel=\"stylesheet\" href=\"/github.css\">").append(eol)
            .append("</head>").append(eol)
            .append("<body>").append(eol)
            .append("<article class=\"markdown-body\">").append(eol);
            renderer.render(contentsBuilder.document(), sb);
            renderer.render(document, sb);
            sb.append("</article>").append(eol)
            .append("</body>").append(eol)
            .append("</html>");
            return sb.toString().getBytes(StandardCharsets.UTF_8);
        }
    }

    @Override
    public MediaType getMediaType() {
        return MediaType.TEXT_HTML;
    }

    @Override
    public boolean cached() {
        return false;
    }
}
