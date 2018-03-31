package com.kluev.wordmemorizer.web.controller.form;

public class TrainingForm {
    private TrainingFormAction action = TrainingFormAction.NONE;

    public TrainingFormAction getAction() {
        return action;
    }

    public void setAction(TrainingFormAction action) {
        this.action = action;
    }

    public enum TrainingFormAction {
        NONE,
        BACK,
        GOOD_RESULT,
        MIDDLE_RESULT,
        BAD_RESULT
    }
}
