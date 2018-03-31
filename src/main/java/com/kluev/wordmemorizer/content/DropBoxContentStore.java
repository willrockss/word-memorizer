package com.kluev.wordmemorizer.content;

import com.dropbox.core.DbxDownloader;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DropBoxContentStore implements ContentStore {
    private DbxRequestConfig config;
    private DbxClientV2 client;

    public DropBoxContentStore(String accessToken) {
        // Create Dropbox client
        config = DbxRequestConfig.newBuilder ("dropbox/card-memorizer").build();
        client = new DbxClientV2(config, accessToken);
    }

    @Override
    public boolean saveIfNotExists(String filename, String value, String extension, InputStream fileStream) {
        System.out.println("Adding file " + filename + " to Store");
        try (InputStream in = fileStream) {
            String path = "/" + filename + "/" + filename + "." + extension;
            FileMetadata metadata = client.files().uploadBuilder(path).uploadAndFinish(in);

            Properties props = new Properties();
            props.setProperty("value", value);
            props.setProperty("extension", extension);
            props.setProperty("size", String.valueOf(metadata.getSize()));

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            props.store(outputStream, "");

            client.files().uploadBuilder(path + ".info").uploadAndFinish(new ByteArrayInputStream(outputStream.toByteArray()));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public InputStream getVideoStreamById(Integer videoId) throws IOException {
        String path = "/" + videoId + "/" + videoId + ".mp4";
        try {
            DbxDownloader<FileMetadata> data = client.files().download(path);
            return data.getInputStream();
        } catch (DbxException e) {
            e.printStackTrace();
            throw new IOException(e);
        }
    }
}
