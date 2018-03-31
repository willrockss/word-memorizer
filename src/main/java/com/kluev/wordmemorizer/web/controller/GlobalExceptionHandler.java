package com.kluev.wordmemorizer.web.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {

    public static final String ERROR_MESSAGE_TEXT = "Файл слишком большой! " +
            "Если загружаете видео непосредственно с камеры на мобильном телефоне, " +
            "то необходимо выставить минимальный уровень качества (например 'MMS') и отключить звук в настройках видео.";

    //StandardServletMultipartResolver
    @ExceptionHandler(MultipartException.class)
    public String handleError1(MultipartException e, RedirectAttributes redirectAttributes) {

        redirectAttributes.addFlashAttribute("error", ERROR_MESSAGE_TEXT);
        return "redirect:/add-word";

    }

    //CommonsMultipartResolver
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleError2(MaxUploadSizeExceededException e, RedirectAttributes redirectAttributes) {

        redirectAttributes.addFlashAttribute("error", ERROR_MESSAGE_TEXT);
        return "redirect:/add-word";

    }

}