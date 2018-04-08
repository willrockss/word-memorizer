package com.kluev.wordmemorizer.utils;

import com.kluev.wordmemorizer.web.model.Captcha;

import java.util.Optional;

public interface CaptchaService {
    Optional<Captcha> getCaptcha();
}
