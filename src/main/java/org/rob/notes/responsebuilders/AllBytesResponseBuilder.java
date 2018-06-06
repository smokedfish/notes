package org.rob.notes.responsebuilders;

import java.io.File;
import java.nio.file.Files;

import org.springframework.http.MediaType;

public class AllBytesResponseBuilder implements ResponseBuilder {

    private final MediaType mediaType;

    public AllBytesResponseBuilder(MediaType mediaType) {
        this.mediaType = mediaType;
    }

    @Override
    public byte[] response(File doc) throws Exception {
        return Files.readAllBytes(doc.toPath());
    }

    @Override
    public MediaType getMediaType() {
        return mediaType;
    }

    @Override
    public boolean cached() {
        return false;
    }
}
