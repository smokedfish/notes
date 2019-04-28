package org.rob.notes.responsebuilders;

import java.io.File;

import org.springframework.http.MediaType;

public interface ResponseBuilder {
    MediaType getMediaType();
    boolean cached();
}
