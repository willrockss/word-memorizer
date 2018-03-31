package com.kluev.wordmemorizer;

import com.kluev.wordmemorizer.content.ContentStore;
import com.kluev.wordmemorizer.content.DropBoxContentStore;
import com.kluev.wordmemorizer.content.FFmpegVideoConverter;
import com.kluev.wordmemorizer.content.VideoConverter;
import com.kluev.wordmemorizer.web.controller.DBUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ContentStoreConfig {

    public static final String DROPBOX_ACCESS_TOKEN_KEY = "dropbox.access.token";
    @Autowired
    DBUtils db;

    @Bean
    public ContentStore contentStore() {

        String accessToken = db.getAppProperty(DROPBOX_ACCESS_TOKEN_KEY);
        if (accessToken == null) {
            throw new IllegalStateException("Не задан ключ " + DROPBOX_ACCESS_TOKEN_KEY + " в таблице настроек приложения");
        }
        return new DropBoxContentStore(accessToken);
    }

    @Bean
    public VideoConverter converter() {
        return new FFmpegVideoConverter();
    }
}
