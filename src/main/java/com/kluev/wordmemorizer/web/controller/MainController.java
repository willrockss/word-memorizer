package com.kluev.wordmemorizer.web.controller;

import com.kluev.wordmemorizer.web.controller.form.MainForm;
import com.kluev.wordmemorizer.web.model.Word;
import com.kluev.wordmemorizer.web.model.Dictionary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.*;

@RestController
@SessionAttributes(types = {Word.class, List.class})
public class MainController {

    @Autowired
    private DBUtils db;

    @RequestMapping("/")
    public ModelAndView index(Principal principal,
                              ModelMap model,
                              HttpSession session,
                              @ModelAttribute MainForm mainForm) {
        List<Dictionary> dicts = getDictList();
        model.addAttribute("dicts", dicts);
        if (MainForm.FormAction.TRAINING == mainForm.getAction()) {
            dicts.stream().filter(d -> d.getId().equals(mainForm.getDictId())).findFirst().ifPresent(d -> session.setAttribute("dict", d));
            return new ModelAndView("redirect:/training", model);
        }
        return new ModelAndView("main", model);
    }

    public List<Dictionary> getDictList() {
        return db.getDictionariesList();
    }

    @RequestMapping("/login")
    public ModelAndView login () {
        return new ModelAndView("login");
    }
}