package org.rob.notes.responsebuilders;

import java.io.File;

import org.springframework.http.MediaType;

public interface ResponseBuilder {
    byte[] response(File doc) throws Exception;
    MediaType getMediaType();
    boolean cached();
}
