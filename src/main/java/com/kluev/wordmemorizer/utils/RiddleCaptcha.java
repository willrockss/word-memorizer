package com.kluev.wordmemorizer.utils;

import com.kluev.wordmemorizer.web.controller.DBUtils;
import com.kluev.wordmemorizer.web.model.Captcha;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

public class RiddleCaptcha implements CaptchaService {
    @Autowired
    private DBUtils dbUtils;

    @Override
    public Optional<Captcha> getCaptcha() {
        return dbUtils.getCaptcha();
    }
}
