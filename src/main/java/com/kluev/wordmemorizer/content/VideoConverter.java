package com.kluev.wordmemorizer.content;

import java.io.IOException;

public interface VideoConverter {
    byte[] convert(byte[] srcData) throws IOException;
}
