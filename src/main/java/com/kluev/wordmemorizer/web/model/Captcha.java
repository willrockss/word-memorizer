package com.kluev.wordmemorizer.web.model;

public class Captcha {
    private String question;
    private String answer;

    public String getQuestion() {
        return question;
    }

    public Captcha setQuestion(String question) {
        this.question = question;
        return this;
    }

    public String getAnswer() {
        return answer;
    }

    public Captcha setAnswer(String answer) {
        this.answer = answer;
        return this;
    }
}
