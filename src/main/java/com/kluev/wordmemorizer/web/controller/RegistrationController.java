package com.kluev.wordmemorizer.web.controller;

import com.kluev.wordmemorizer.web.model.Captcha;
import com.kluev.wordmemorizer.utils.CaptchaService;
import com.kluev.wordmemorizer.web.controller.form.RegistrationForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Collections;
import java.util.Optional;

@RestController
public class RegistrationController {

    public static final int MIN_PASS_LEN = 6;

    @Autowired
    private UserDetailsManager userDetailsManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CaptchaService captchaService;

    @GetMapping("/registration")
    public ModelAndView registrationGet(HttpServletRequest request,
                                        ModelMap model,
                                        HttpSession session,
                                        @ModelAttribute RegistrationForm regForm)
    {
        model.addAttribute("regForm", regForm);

        Optional<Captcha> capOtp = captchaService.getCaptcha();
        capOtp.ifPresent(c -> {
            model.addAttribute("captchaQuestion", c.getQuestion());
            session.setAttribute("captchaCorrectAnswer", c.getAnswer());
        });

        return new ModelAndView("registration", model);
    }

    @PostMapping("/registration")
    public ModelAndView registrationPost(HttpServletRequest request,
                                         ModelMap model,
                                         HttpSession session,
                                         @ModelAttribute RegistrationForm regForm,
                                         RedirectAttributes redirectAttributes)
    {
        String username = regForm.getUsername().trim();
        if (username.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Логин не должен быть пустым!");
            return new ModelAndView("redirect:/registration");
        }

        String password = regForm.getPassword().trim();
        if (password.length() < MIN_PASS_LEN) {
            redirectAttributes.addFlashAttribute("error",
                    "Длинна пароля должна быть не меньше " + MIN_PASS_LEN + " символов!");
            return new ModelAndView("redirect:/registration");
        }

        String password2 = regForm.getPassword2().trim();
        if (!password.equals(password2)) {
            redirectAttributes.addFlashAttribute("error", "Пароль и подтверждение пароля не совпадают!");
            return new ModelAndView("redirect:/registration");
        }

        String captchaCorrectAnswer = (String) session.getAttribute("captchaCorrectAnswer");
        if (captchaCorrectAnswer != null) {
            String captchaAnswer = regForm.getCaptchaAnswer().trim().toLowerCase();
            if (captchaAnswer.isEmpty() || !captchaCorrectAnswer.contains(captchaAnswer)) {
                    redirectAttributes.addFlashAttribute("error", "Капча введена неверно!");
                return new ModelAndView("redirect:/registration");
            }
        }


        if (userDetailsManager.userExists(username)) {
            redirectAttributes.addFlashAttribute("error",
                    "Пользователь с логим " + username + " уже существует!");
            return new ModelAndView("redirect:/registration");
        }

        User newUser = new User(username, passwordEncoder.encode(password), Collections.singleton(new SimpleGrantedAuthority("USER")));
        userDetailsManager.createUser(newUser);

        try {
            //TODO Написать обработку ситуации, когда пользователь был уже залогинен
            request.login(username, password);
        } catch (ServletException e) {
            e.printStackTrace();
        }

        model.clear();
        return new ModelAndView("redirect:/");
    }
}

