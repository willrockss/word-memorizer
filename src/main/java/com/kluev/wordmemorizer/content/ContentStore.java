package com.kluev.wordmemorizer.content;

import java.io.IOException;
import java.io.InputStream;

public interface ContentStore {
    boolean saveIfNotExists(String filename, String value, String extension, InputStream fileStream);

    InputStream getVideoStreamById(Integer videoId) throws IOException;
}
