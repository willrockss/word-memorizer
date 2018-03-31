package com.kluev.wordmemorizer.web.controller;

import com.kluev.wordmemorizer.web.model.Dictionary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;

@Controller
public class DictManagementController {

    @Autowired
    DBUtils db;

    @RequestMapping("/dict-management")
    public ModelAndView dictlist(ModelMap model) {
        return new ModelAndView("dict-management", model);
    }

    @RequestMapping("/add-dict")
    public ModelAndView addDict(@ModelAttribute Dictionary dict) {
        if (dict == null || !dict.isSaved()) {
            dict.setName("default name");
        }
        return new ModelAndView("add-dict", Collections.singletonMap("dict", dict));
    }
    @PostMapping("/add-dict")
    public ModelAndView saveDict(@ModelAttribute Dictionary dict, RedirectAttributes redirectAttributes) {
        if (db.isDictPresent(dict.getName())) {
            redirectAttributes.addFlashAttribute("error", "Словарь с таким именем уже существует! Выберите другое.");
        } else {
            try {
                boolean res = db.saveDict(dict.getName());
                if (res) {
                    redirectAttributes.addFlashAttribute("message", "Словарь \"" + dict.getName() + "\" успешно добавлен");
                }
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error", "Что-то пошло не так: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return new ModelAndView("redirect:/add-dict");
    }
}
