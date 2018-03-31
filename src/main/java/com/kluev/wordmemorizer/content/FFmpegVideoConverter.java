package com.kluev.wordmemorizer.content;

import it.sauronsoftware.jave.*;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.file.*;
import java.util.UUID;


/**
 *  Использовались переработанная библиотека для конвертирования видео
 *  (см. http://www.sauronsoftware.it/projects/jave/manual.php)
 * */
public class FFmpegVideoConverter implements VideoConverter {

    @Override
    public byte[] convert(byte[] srcData) throws IOException {
        String uuid = UUID.randomUUID().toString();

        Path srcPath = Paths.get(uuid + "_src");
        IOUtils.write(srcData, Files.newOutputStream(srcPath));

        Path target = Paths.get(uuid + "_dest.mp4");

        VideoAttributes video = new VideoAttributes();

        EncodingAttributes attrs = new EncodingAttributes();
        attrs.setFormat("mp4");
        attrs.setAudioAttributes(null);
        attrs.setVideoAttributes(video);

        try {

            Encoder encoder = new Encoder();
            encoder.encode(srcPath.toFile(), target.toFile(), attrs);

            byte[] convertedData;

            //Для того, что бы сработал механизм AutoCloseable
            try (InputStream is = new FileInputStream(target.toFile())) {
                convertedData = IOUtils.toByteArray(is);
            }

            return convertedData;
        } catch (EncoderException ee) {
            throw new IOException(ee.getMessage(), ee);
        } finally {
            Files.deleteIfExists(srcPath);
            Files.deleteIfExists(target);
        }
    }
}
