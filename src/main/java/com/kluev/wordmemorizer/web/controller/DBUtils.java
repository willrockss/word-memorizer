package com.kluev.wordmemorizer.web.controller;

import com.kluev.wordmemorizer.web.model.Captcha;
import com.kluev.wordmemorizer.web.model.Word;
import com.kluev.wordmemorizer.web.model.Dictionary;
import com.kluev.wordmemorizer.web.model.mapper.CaptchaRowMapper;
import com.kluev.wordmemorizer.web.model.mapper.WordRowMapper;
import com.kluev.wordmemorizer.web.model.mapper.DictRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

//TODO Переписать с использованием NamedParameterJdbcTemplate
@Component
public class DBUtils {
    @Autowired
    JdbcTemplate jdbcTemplate;

    public Integer getWordCount() {
        return jdbcTemplate.queryForObject("select count(*) from word", Integer.class);
    }

    public Word getWordToLearn(String username, Integer dictId) {
        return getWordToLearn(username, dictId, false, false);
    }

    public Word getWordToLearnUsingNew(String username, Integer dictId) {
        return getWordToLearn(username, dictId, true, false);
    }

    public Word getWordToLearnIgnoreTime(String username, Integer dictId) {
        return getWordToLearn(username, dictId, false, true);
    }

    public Word getWordToLearn(String username, Integer dictId, boolean takeNew, boolean ignoreTime) {
        String dictJoinSql = dictId > 0 ? "join word_dict wd on wd.video_id = sw.video_id and wd.dict_id = " + dictId + "\n": "";

        String conditionSql = "where ";
        if (takeNew) {
            conditionSql += "wp.first_time is null\n";
        } else {
            conditionSql += "wp.first_time is not null \n";
            if (!ignoreTime) {
                conditionSql += "and now() > (wp.last_time + (interval '1 hour' * wp.count))\n";
            }
            conditionSql += " and username = '" + username + "'\n";
        }

        String sql =
                "select sw.* from word sw\n" +
                dictJoinSql +
                "left join word_progress wp on wp.video_id = sw.video_id\n" +
                conditionSql +
                "order by last_time\n" +
                "limit 1 \n";
        List<Word> resList = jdbcTemplate.query(sql, new WordRowMapper());
        return resList.isEmpty() ? null : resList.get(0);
    }

    List<Word> getWordsToLearn(Integer dictId) {
        String dictSql = dictId != null ? "join word_dict wd on wd.video_id = sw.video_id and wd.dict_id = " + dictId + "\n": "";

        String sql =
                "select sw.* from word sw\n" +
                dictSql +
                "left join word_progress wp on wp.video_id = sw.video_id\n" +
                "order by last_time\n" +
                "limit 10 \n";
        return jdbcTemplate.query(sql, new WordRowMapper());
    }

    Word loadWordByRowId(Integer rowId) {
        return jdbcTemplate.query("select * from word offset " + rowId + " limit 1;", new WordRowMapper()).iterator().next();
    }

    String getVideoUrlByVideoId(Integer videoId) {
        return jdbcTemplate.queryForObject("select video_url from word where video_id = ?", String.class, videoId);
    }

    public void updateWordView(String usernem, Word word) {
        changeWordView(usernem, word, 0);
    }

    public void decrementWordView(String username, Word word) {
        changeWordView(username, word, -1);
    }

    public void incrementWordView(String username, Word word){
        changeWordView(username, word, 1);
    }

    public void changeWordView(String username, Word word, int delta) {
        String updateSql = "update word_progress set count = count + " + delta +
                ", last_time = now() where (count + "+delta+" >= 0) and video_id = " + word.getVideoId() +
                "\n and username = '" + username + "'";
        boolean updated = jdbcTemplate.update(updateSql) == 1;

        if (!updated) {
            String insertSql = "insert into word_progress(username, video_id, count, last_time) " +
                    "select '" + username +"', " + word.getVideoId() +
                    ", 1, now() where not exists (select 1 from word_progress where video_id = " + word.getVideoId() +
                    " and username = '" + username + "')";
            jdbcTemplate.update(insertSql);
        }
    }

    public void updateWatchedTimeToWord(String username, Word word) {
        String updateLastSql = "update word_progress set last_time = now() where video_id = "
                + word.getVideoId() + " and username = '" + username + "'";
        jdbcTemplate.update(updateLastSql);

        String updateFirstSql = "update word_progress set first_time = now() where first_time is null and video_id= "
                + word.getVideoId() + " and username = '" + username + "'";
        jdbcTemplate.update(updateFirstSql);
    }

    public List<Dictionary> getDictionariesList() {
        String sql = "select id, name from dict";
        return jdbcTemplate.query(sql, new DictRowMapper());
    }


    /**
     * Метод возвращает список словарей, которые текущий пользователь может редактировать.
     * Редактировать словарь может либо пользователь с ролью <code>ADMIN</code>, либо пользователь, указанный в
     * качестве значения атрибута <code>AUTHOR</code> в словаре.
     * */
    public List<Dictionary> getEditableDictionariesList() {
        String username = getCurrentUsername();
        if (username == null) {
            return Collections.emptyList();
        }

        String sql = "SELECT * FROM dict d\n" +
                     "JOIN authorities auth ON auth.username = ?\n" +
                     "LEFT JOIN dict_attribute_value dav ON dav.dict_id = d.id AND dav.attribute_code = 'AUTHOR'\n" +
                     "WHERE auth.authority = 'ADMIN' OR dav.string_value = ?";
        return jdbcTemplate.query(sql, new Object[]{username, username}, new DictRowMapper());
    }

    //TODO получать пользователя методом getUsername
    public void addLoginInfo(String username) {
        if (username != null) {
            String sql = "INSERT INTO action_log(username, action_name, action_time)\n" +
                    "SELECT '" + username + "','LOGIN', now()";
            try {
                jdbcTemplate.update(sql);
            } catch (Exception e) {
                    e.printStackTrace();
            }
        } else {
            System.out.println("Username is null!!!");
        }
    }

    public Integer getNextWordId() {
        String sql = "select nextval('word_id_seq')";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    public void addWord(Integer wordId, String word, Integer videoId) {
        String sql =
                "INSERT INTO word(id, value, video_id, video_url, loaded, created)\n" +
                "SELECT " + wordId + ", '" + word + "', " + videoId + ",'/video/" + wordId + ".mp4' ,true, now()\n" +
                "WHERE NOT EXISTS (SELECT 1 FROM word WHERE video_id = " + videoId + ")";
        jdbcTemplate.update(sql);
    }

    public void addWordToDictionary(Integer videoId, Integer dictId) {
        String sql =
                "insert into word_dict(video_id, dict_id)\n" +
                "select " + videoId + ", " + dictId + "\n" +
                "where not exists (select 1 from word_dict where video_id = " + videoId + " and dict_id = " + dictId + ")";
        jdbcTemplate.update(sql);
    }

    public boolean isDictPresent(String dictName) {
        String sql = "SELECT 1 FROM dict WHERE name ilike trim(from ?)";
        List val = jdbcTemplate.queryForList(sql, dictName);
        return !val.isEmpty();
    }

    public Integer saveDict(String name) {
        String username = getCurrentUsername();
        if (username == null) {
            //TODO log
            return null;
        }

        Integer dictId = null;
        String sql ="INSERT INTO dict(name)\n" +
                    "SELECT ? \n" +
                    "WHERE NOT EXISTS (SELECT 1 FROM dict WHERE name ilike trim(from ?))";
        if (jdbcTemplate.update(sql, name, name) == 1) {
            dictId = jdbcTemplate.queryForObject("SELECT id FROM dict WHERE name = ?", Integer.class, name);
        }

        // Записываем в атрибут "Автор" словаря текущего пользователя
        String sqlSetAuthor = "INSERT INTO dict_attribute_value(dict_id, attribute_code, string_value) \n" +
                              "SELECT ?, 'AUTHOR', ? \n" +
                              "WHERE NOT EXISTS (SELECT 1 FROM dict_attribute_value WHERE dict_id = ? AND attribute_code = 'AUTHOR')";
        jdbcTemplate.update(sqlSetAuthor, dictId, username, dictId);
        return dictId;
    }

    public String getAppProperty(String key) {
        String sql = "SELECT value FROM app_property WHERE key = 'dropbox.access.token'";
        try {
            return jdbcTemplate.queryForObject(sql, String.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public Optional<Captcha> getCaptcha() {
        String sql = "SELECT question, answer FROM captcha ORDER BY random() LIMIT 1";
        List<Captcha> res = jdbcTemplate.query(sql, new CaptchaRowMapper());
        if (!res.isEmpty()) {
            return Optional.ofNullable(res.get(0));
        }
        return Optional.empty();
    }

    public String getCurrentUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDetails details =  principal instanceof  UserDetails ? (UserDetails) principal : null;
        return details != null ? details.getUsername() : null;
    }
}
