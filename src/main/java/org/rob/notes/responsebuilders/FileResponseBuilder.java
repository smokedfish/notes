package org.rob.notes.responsebuilders;

import java.io.File;

public interface FileResponseBuilder extends ResponseBuilder {
    byte[] response(File doc) throws Exception;
}
