package com.practice.springjdbc.dao;

import com.practice.springjdbc.exception.NotFoundException;
import com.practice.springjdbc.model.Option;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OptionDaoImpl implements OptionDao {
    CategoryDao categoryDao;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Option> findAll() {
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet("select * from options");

        List<Option> options = new ArrayList<>();
        while (sqlRowSet.next()) {
            int id = sqlRowSet.getInt("id");
            String name = sqlRowSet.getString("name");
            options.add(new Option(id, name, null));
        }

        return options;
    }

    @Override
    public Option findById(int id) {
        String sql = "select * from categories where id = ?";
        return jdbcTemplate.query(sql, this::mapRow, id)
                .stream()
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Опция не найдена"));
    }

    @Override
    public Option create(Option option) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("categories")
                .usingGeneratedKeyColumns("id");

        Map<String, Object> map = Map.of("name", option.getName());
        int id = insert.executeAndReturnKey(map).intValue();
        option.setId(id);

        return option;
    }

    public Option mapRow(ResultSet rs, int rowNum) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        int categoryId = rs.getInt("category_id");
        return new Option(id, name, categoryDao.findById(categoryId));
    }
}
