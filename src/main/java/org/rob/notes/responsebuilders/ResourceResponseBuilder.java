package org.rob.notes.responsebuilders;

import com.google.common.io.ByteStreams;
import org.rob.notes.DocController;
import org.springframework.http.MediaType;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ResourceResponseBuilder implements ResponseBuilder {

    private final MediaType mediaType;

    public ResourceResponseBuilder(MediaType mediaType) {
        this.mediaType = mediaType;
    }

    public byte[] response(String path) throws Exception {
        try (InputStream inputStream = this.getClass().getResourceAsStream(path)) {
            if (inputStream != null) {
                return ByteStreams.toByteArray(inputStream);
            }
        }
        return null;
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
