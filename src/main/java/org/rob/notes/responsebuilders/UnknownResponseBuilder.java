package org.rob.notes.responsebuilders;

import java.io.File;
import java.nio.charset.StandardCharsets;

import org.springframework.http.MediaType;

public class UnknownResponseBuilder implements ResponseBuilder {
    @Override
    public byte[] response(File doc) throws Exception {
        return ("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>"
                + "<svg height=\"30\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink= \"http://www.w3.org/1999/xlink\">"
                + "<text x=\"0\" y=\"15\" fill=\"red\">Unknown extension for" + doc + "</text>"
                + "</svg>").getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public MediaType getMediaType() {
        return new MediaType("image", "svg+xml");
    }
}
