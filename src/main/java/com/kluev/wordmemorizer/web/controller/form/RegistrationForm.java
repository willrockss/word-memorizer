package com.kluev.wordmemorizer.web.controller.form;

public class RegistrationForm {
    private String username = "";
    private String password = "";
    private String password2 = "";

    private String captchaAnswer = "";

    public String getUsername() {
        return username;
    }

    public RegistrationForm setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public RegistrationForm setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getPassword2() {
        return password2;
    }

    public RegistrationForm setPassword2(String password2) {
        this.password2 = password2;
        return this;
    }

    public String getCaptchaAnswer() {
        return captchaAnswer;
    }

    public RegistrationForm setCaptchaAnswer(String captchaAnswer) {
        this.captchaAnswer = captchaAnswer;
        return this;
    }
}
