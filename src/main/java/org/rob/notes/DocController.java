package org.rob.notes;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.rob.notes.responsebuilders.ResponseBuilder;
import org.rob.notes.responsebuilders.UnknownResponseBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DocController {
    private static final ResponseBuilder UNKNOWN_RESPONSE_BUILDER = new UnknownResponseBuilder();

    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public class NotFoundException extends RuntimeException {
    }

    private final Map<String, ResponseBuilder> responseBuilders;
    private final File docRootDir;
    private final File cacheRootDir;

    DocController(
            @Value(value = "${notesDir:/home/rob/notes}") File docRootDir,
            @Value(value = "${cacheDir:/home/rob/notes/cache}") File cacheRootDir,
            Map<String, ResponseBuilder> responseBuilders) {
        this.docRootDir = docRootDir;
        this.cacheRootDir = cacheRootDir;
        this.responseBuilders = responseBuilders;
    }

    @RequestMapping
    ResponseEntity<InputStreamResource> doc(HttpServletRequest request) throws Exception {
        String path = request.getRequestURI().substring(request.getContextPath().length());
        String extension = getExtension(path);

        File doc = new File(docRootDir, path);
        if (doc.exists() && doc.isFile() && doc.canRead()) {
            ResponseBuilder responseBuilder = responseBuilders.getOrDefault(extension, UNKNOWN_RESPONSE_BUILDER);
            File cachedDoc = new File(cacheRootDir, path);
            byte[] bytes;
            if (cachedDoc.exists() && cachedDoc.isFile() && cachedDoc.canRead() && cachedDoc.lastModified() > doc.lastModified()) {
                bytes = Files.readAllBytes(cachedDoc.toPath());
            } else {
                bytes = responseBuilder.response(doc);
                Files.write(cachedDoc.toPath(), bytes);
            }
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(responseBuilder.getMediaType());
            headers.setContentLength(bytes.length);
            return new ResponseEntity<>(new InputStreamResource(new ByteArrayInputStream(bytes)), headers, HttpStatus.OK);
        }
        throw new NotFoundException();
    }

    private String getExtension(String path) {
        int index = path.lastIndexOf('.');
        return index > 0 ? path.substring(index + 1) : "";
    }

}
