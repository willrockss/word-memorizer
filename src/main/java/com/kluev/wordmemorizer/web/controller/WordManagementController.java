package com.kluev.wordmemorizer.web.controller;

import com.kluev.wordmemorizer.content.ContentStore;
import com.kluev.wordmemorizer.content.FFmpegVideoConverter;
import com.kluev.wordmemorizer.content.VideoConverter;
import com.kluev.wordmemorizer.web.controller.form.WordManagementForm;
import com.kluev.wordmemorizer.web.model.Dictionary;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.Principal;
import java.util.List;

@Controller
public class WordManagementController {

    public static final String MP4 = "mp4";
    @Autowired
    private DBUtils db;

    @Autowired
    private ContentStore contentStore;

    @Autowired
    private VideoConverter videoConverter;

    @GetMapping("/add-word")
    public ModelAndView addWordGet(ModelMap model, @RequestParam(required = false, defaultValue = "-2") String dictId)
    {
        model.addAttribute("dictId", dictId);

        List<Dictionary> dicts = getDictList();
        model.addAttribute("dicts", dicts);
        return new ModelAndView("add-word", model);
    }

    public List<Dictionary> getDictList() {
        return db.getEditableDictionariesList();
    }

    @PostMapping("/add-word")
    public ModelAndView addWordPost(@RequestParam("file") MultipartFile file,
                                    @RequestParam("word") String word,
                                    @ModelAttribute WordManagementForm wordManagementForm,
                                    RedirectAttributes redirectAttributes) {
        String errorValue = null;
        if (file.isEmpty()) {
            errorValue = "Выберите видео для загрузки";
        } else if (word == null || word.trim().isEmpty()) {
            errorValue = "Укажите не пустое значение жеста";
        } else if (wordManagementForm.getDictId() == null || wordManagementForm.getDictId() < 0) {
            errorValue = "Выберите словарь, в который хотите добавить новый жест";
        } else {
            Integer nextWordId = db.getNextWordId();
            Integer videoId = -nextWordId;

            try {
                String extension = getExtensionFromFilename(file.getOriginalFilename());
                InputStream fileContent;
                if (!extension.toLowerCase().trim().equals(MP4)) {
                    byte[] initialContent = IOUtils.toByteArray(file.getInputStream());
                    fileContent = new ByteArrayInputStream(videoConverter.convert(initialContent));
                } else {
                    fileContent = file.getInputStream();
                }

                contentStore.saveIfNotExists(nextWordId.toString(), word, MP4, fileContent);

                //Если всё успешно сконвертировалось, добавляет слово в БД
                db.addWord(nextWordId, word, videoId);
                db.addWordToDictionary(videoId, wordManagementForm.getDictId());
            } catch (Exception e) {
                errorValue = "Невозможно добавить слово из-за: " + e.getMessage();
                e.printStackTrace();
            }

            redirectAttributes.addFlashAttribute("message",
                    "Слово успешно добавлено " + file.getOriginalFilename() + "!");

        }

        if (errorValue != null) {
            redirectAttributes.addFlashAttribute("error", errorValue);
        }
        return new ModelAndView("redirect:/add-word");
    }

    private String getExtensionFromFilename(String name) {
        int pos = name.lastIndexOf(".");
        return name.substring(pos + 1);
    }
}
