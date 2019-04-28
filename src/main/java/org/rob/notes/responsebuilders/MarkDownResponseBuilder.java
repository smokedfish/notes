package org.rob.notes.responsebuilders;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import org.commonmark.Extension;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.http.MediaType;

public class MarkDownResponseBuilder implements FileResponseBuilder {
    private final Parser parser;
    private final HtmlRenderer renderer;

    public MarkDownResponseBuilder() {
        List<Extension> extensions = Arrays.asList(TablesExtension.create());

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
            StringBuilder sb = new StringBuilder()
            .append("<!DOCTYPE html>")
            .append("<html>")
            .append("<head>")
            .append("<link rel=\"stylesheet\" href=\"/github.css\">")
            .append("<style>\n" +
                    "\t\t\tbody {\n" +
                    "\t\t\t\tbox-sizing: border-box;\n" +
                    "\t\t\t\tmin-width: 200px;\n" +
                    "\t\t\t\tmax-width: 980px;\n" +
                    "\t\t\t\tmargin: 0 auto;\n" +
                    "\t\t\t\tpadding: 45px;\n" +
                    "\t\t\t}\n" +
                    "\t\t</style>")
                    .append("</head>")
            .append("<body>")
            .append("<article class=\"markdown-body\">");
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

    @Override
    public boolean cached() {
        return false;
    }
}
