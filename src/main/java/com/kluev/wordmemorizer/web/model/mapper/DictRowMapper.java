package com.kluev.wordmemorizer.web.model.mapper;

import com.kluev.wordmemorizer.web.model.Dictionary;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DictRowMapper  implements RowMapper<Dictionary> {
    @Override
    public Dictionary mapRow(ResultSet rs, int rowNum) throws SQLException {
        Dictionary d = new Dictionary();
        d.setId(rs.getInt("id"));
        d.setName(rs.getString("name"));
        return d;
    }
}
