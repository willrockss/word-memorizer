package com.kluev.wordmemorizer.web.controller.form;

public class DictManagementForm {
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

    public DictManagementForm setAction(FormAction action) {
        this.action = action;
        return this;
    }

    public enum FormAction {
        NONE
    }
}
