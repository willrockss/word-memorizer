package com.kluev.wordmemorizer.web.controller.form;

public class WordManagementForm {
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

    public WordManagementForm setAction(FormAction action) {
        this.action = action;
        return this;
    }

    public enum FormAction {
        NONE
    }
}
