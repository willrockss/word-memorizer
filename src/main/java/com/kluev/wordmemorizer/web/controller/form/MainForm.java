package com.kluev.wordmemorizer.web.controller.form;

public class MainForm {
    private FormAction action = FormAction.NONE;
    private Integer dictId;

    public Integer getDictId() {
        return dictId;
    }

    public void setDictId(Integer dictId) {
        this.dictId = dictId;
    }

    public FormAction getAction() {
        return action;
    }

    public void setAction(FormAction action) {
        this.action = action;
    }

    public static enum FormAction {
        NONE,
        TRAINING
    }
}
