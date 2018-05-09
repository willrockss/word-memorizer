package com.kluev.wordmemorizer.web.controller;

import com.kluev.wordmemorizer.content.ContentStore;
import com.kluev.wordmemorizer.web.controller.form.TrainingForm;
import com.kluev.wordmemorizer.web.model.Word;
import com.kluev.wordmemorizer.web.model.Dictionary;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.security.Principal;
import java.util.List;

@RestController
@SessionAttributes(types = {Word.class, List.class})
public class TrainingController {

    @Autowired
    DBUtils db;

    @Autowired
    private ContentStore contentStore;

    @RequestMapping("/training")
    public ModelAndView training(Principal principal,
                                 RedirectAttributes redirectAttributes,
                                 ModelMap model,
                                 HttpSession session,
                                 @ModelAttribute TrainingForm trainingForm)
    {

        if (TrainingForm.TrainingFormAction.BACK == trainingForm.getAction()) {
            model.clear();
            return new ModelAndView("redirect:/");
        }

        Dictionary dict = (Dictionary) session.getAttribute("dict");
        if (dict == null) {
            // Если по каким-то причинам не установлен словарь для тренировки,
            // то выбрасываем пользователя на главную страницу
            model.clear();
            return new ModelAndView("redirect:/");
        }
        Integer dictId = dict.getId();

        String username = principal.getName();

        Word prevWord = (Word) session.getAttribute("word");
        if (prevWord != null) {
            if (TrainingForm.TrainingFormAction.GOOD_RESULT == trainingForm.getAction()) {
                // Увеличиваем количество просмотров карточки
                db.incrementWordView(username, prevWord); //TODO тут учитывать режим отключения счетчика просмотров
                db.updateWatchedTimeToWord(username, prevWord);
            } else if (TrainingForm.TrainingFormAction.BAD_RESULT == trainingForm.getAction()) {
                // Уменьшаем количество просмотров
                db.decrementWordView(username, prevWord);//TODO тут учитывать режим отключения счетчика просмотров
                db.updateWatchedTimeToWord(username, prevWord);
            } else {
                // Помечаем слово как изученное, не меняя счетчик просмотров
                // Просто обновляем время последнего просмотра
                db.updateWordView(username, prevWord);
                db.updateWatchedTimeToWord(username, prevWord);
            }
        }

        try {
            Word word = db.getWordToLearn(username, dictId);
            if (word == null) {
                word = db.getWordToLearnUsingNew(username, dictId);
                //TODO Выбрасывать предложение позаниматься с другим словарем,
                // но при этом взять слово игнорируя время последнего изучения
                // Тут на форму выставить флаг, при котором счетчики просмотров не будут менятсья в таком режиме
                // Кнопку "назад" при этом начать подсвечивать зеленым, а кнопки запоминаний - серыми, т.е. никак не подсвечивать!!!
                if (word == null) {
                    word = db.getWordToLearnIgnoreTime(username, dictId);
                }

                // Если вообще нет слов, например потому что словарь пустой
                if (word == null) {
                    model.clear();
                    redirectAttributes.addFlashAttribute("error",
                            "В словаре \"" + dict.getName() + "\" нет слов для изучения. Выберите другой.");
                    return new ModelAndView("redirect:/");
                }
            }

//            word.setVideoUrl("/video/" + Math.abs(word.getVideoId()));
            model.addAttribute("word", word);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return new ModelAndView("training", model);
    }

    @RequestMapping(value = "/video/{video_code}.mp4", method = RequestMethod.GET)
    public void getVideoContent(
            @PathVariable("video_code") Integer videoId,
            HttpServletResponse response)
    {
        response.setContentType("video/mp4");
        response.setHeader("Cache-Control", "max-age=34680"); //кэшируем на 14 дней

        try (OutputStream output = response.getOutputStream()) {
            // Данный подход с массивом байт приходится использоваться из-за того, что
            // DropBox почему-то закрывает поток со своей стороны при попытке сразу
            // записать из потока в поток.
            byte[] data = IOUtils.toByteArray(contentStore.getVideoStreamById(videoId));
            IOUtils.write(data, output);
            output.flush();
        } catch (IOException ex) {
            throw new RuntimeException("IOError writing file to output stream", ex);
        }
    }
}
