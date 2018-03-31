package com.kluev.wordmemorizer.web.model.mapper;

import com.kluev.wordmemorizer.web.model.Word;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WordRowMapper implements RowMapper<Word> {
    @Override
    public Word mapRow(ResultSet rs, int rowNum) throws SQLException {
        Word w = new Word();
        w.setValue(rs.getString("value"));
        w.setVideoUrl(rs.getString("video_url"));
        w.setVideoId(rs.getInt("video_id"));
        return w;
    }
}
