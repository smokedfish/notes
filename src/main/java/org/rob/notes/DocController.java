package org.rob.notes;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.rob.notes.responsebuilders.ResponseBuilder;
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
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public class NotFoundException extends RuntimeException {
        public NotFoundException(String message) {
            super(message);
        }
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
        if (!docRootDir.exists() || !docRootDir.isDirectory()) {
            throw new NotFoundException("docRootDir does not exist " + docRootDir);
        }
        if (!cacheRootDir.exists() || !cacheRootDir.isDirectory()) {
            throw new NotFoundException("cacheRootDir does not exist " + cacheRootDir);
        }
    }

    @RequestMapping
    ResponseEntity<InputStreamResource> doc(HttpServletRequest request) throws Exception {
        String path = request.getRequestURI().substring(request.getContextPath().length());
        return readPath(path);
    }

    private ResponseEntity<InputStreamResource> readPath(String path) throws Exception {
        File doc = new File(docRootDir, path);
        if (doc.exists() && doc.canRead()) {
            if (doc.isFile()) {
                return readFile(path, doc);
            } else if (doc.isDirectory() && doc.canRead()) {
                HttpHeaders headers = new HttpHeaders();
                headers.add("Location", path + "/index.md");
                return new ResponseEntity<>(new InputStreamResource(new ByteArrayInputStream(new byte[0])), headers, HttpStatus.FOUND);
            }
        }
        throw new NotFoundException("Can't find " + path);
    }

    private ResponseEntity<InputStreamResource> readFile(String path, File doc) throws Exception {
        String extension = getExtension(path);
        ResponseBuilder responseBuilder = responseBuilders.get(extension);
        if (responseBuilder != null) {
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
        }
        throw new NotFoundException("Unknown extension in " + path);
    }

    private String getExtension(String path) {
        int index = path.lastIndexOf('.');
        return index > 0 ? path.substring(index + 1) : "";
    }

}
