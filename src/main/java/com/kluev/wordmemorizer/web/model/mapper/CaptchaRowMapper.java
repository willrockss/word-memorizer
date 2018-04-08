package com.kluev.wordmemorizer.web.model.mapper;

import com.kluev.wordmemorizer.web.model.Captcha;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CaptchaRowMapper implements RowMapper<Captcha> {
    @Override
    public Captcha mapRow(ResultSet rs, int rowNum) throws SQLException {
        Captcha c = new Captcha();
        c.setQuestion(rs.getString("question"));
        c.setAnswer(rs.getString("answer"));
        return c;
    }
}
