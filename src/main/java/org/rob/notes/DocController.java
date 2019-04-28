package org.rob.notes;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.rob.notes.responsebuilders.FileResponseBuilder;
import org.rob.notes.responsebuilders.ResourceResponseBuilder;
import org.rob.notes.responsebuilders.ResponseBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final static Logger log = LoggerFactory.getLogger(DocController.class);

    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public static class NotFoundException extends RuntimeException {
        public NotFoundException(String message) {
            super(message);
        }
    }

    private final Map<String, ResponseBuilder> responseBuilders;
    private final File docRootDir;
    private final File cacheRootDir;

    public DocController(
            @Value(value = "${notesDir:}") File docRootDir,
            @Value(value = "${cacheDir:}") File cacheRootDir,
            Map<String, ResponseBuilder> responseBuilders) {
        this.responseBuilders = responseBuilders;
        this.docRootDir = docRootDir == null ? new File(".") : docRootDir;
        this.cacheRootDir = cacheRootDir == null ? new File("./cache") : cacheRootDir;

        if (!this.docRootDir.exists() || !this.docRootDir.isDirectory()) {
            throw new NotFoundException("docRootDir does not exist " + this.docRootDir);
        }
        if (!this.cacheRootDir.exists() || !this.cacheRootDir.isDirectory()) {
            throw new NotFoundException("cacheRootDir does not exist " + this.cacheRootDir);
        }
    }

    @RequestMapping
    ResponseEntity<InputStreamResource> doc(HttpServletRequest request) throws Exception {
        String path = request.getRequestURI().substring(request.getContextPath().length());
        return readPath(path);
    }

    private ResponseEntity<InputStreamResource> readPath(String path) throws Exception {
        String extension = getExtension(path);
        ResponseBuilder responseBuilder = responseBuilders.get(extension);
        if (responseBuilder instanceof FileResponseBuilder) {
            return readFile((FileResponseBuilder) responseBuilder, path);
        } else if (responseBuilder instanceof ResourceResponseBuilder) {
            return readResource((ResourceResponseBuilder) responseBuilder, path);
        }
        log.warn("Unknown extension in {}", path);
        throw new NotFoundException("Unknown extension in " + path);
    }

    private String getExtension(String path) {
        int index = path.lastIndexOf('.');
        return index > 0 ? path.substring(index + 1) : "";
    }

    private ResponseEntity<InputStreamResource> readResource(ResourceResponseBuilder responseBuilder, String path) throws Exception {
        byte[] bytes = responseBuilder.response(path);
        if (bytes != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(responseBuilder.getMediaType());
            headers.setContentLength(bytes.length);
            return new ResponseEntity<>(new InputStreamResource(new ByteArrayInputStream(bytes)), headers, HttpStatus.OK);
        }
        log.warn("Unable to find {}", path);
        throw new NotFoundException("Can't find " + path);
    }

    private ResponseEntity<InputStreamResource> readFile(FileResponseBuilder responseBuilder, String path) throws Exception {
        File doc = new File(docRootDir, path);
        if (doc.exists() && doc.canRead()) {
            if (doc.isFile()) {
                File cachedDoc = new File(cacheRootDir, path);
                byte[] bytes;
                if (cachedDoc.exists() && cachedDoc.isFile() && cachedDoc.canRead() && cachedDoc.lastModified() > doc.lastModified()) {
                    bytes = Files.readAllBytes(cachedDoc.toPath());
                } else {
                    bytes = responseBuilder.response(doc);
                    if (responseBuilder.cached()) {
                        cachedDoc.getParentFile().mkdirs();
                        Files.write(cachedDoc.toPath(), bytes);
                    }
                }
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(responseBuilder.getMediaType());
                headers.setContentLength(bytes.length);
                return new ResponseEntity<>(new InputStreamResource(new ByteArrayInputStream(bytes)), headers, HttpStatus.OK);
            } else if (doc.isDirectory() && doc.canRead()) {
                HttpHeaders headers = new HttpHeaders();
                headers.add("Location", path + "/index.md");
                return new ResponseEntity<>(new InputStreamResource(new ByteArrayInputStream(new byte[0])), headers, HttpStatus.FOUND);
            }
        }
        log.warn("Unable to find {}", path);
        throw new NotFoundException("Can't find " + path);
    }
}
