package com.practice.springjdbc.dao;

import com.practice.springjdbc.exception.NotFoundException;
import com.practice.springjdbc.model.Category;
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
public class CategoryDaoImpl implements CategoryDao {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Category> findAll() {
        List<Category> categories = new ArrayList<>();
        String sql = """
                select c.id as category_id,
                       c.name as category_name,
                       o.id as option_id,
                       o.name as option_name
                from categories c
                join options o on o.category_id = c.id
                """;

        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql);

        while (sqlRowSet.next()) {
            int id = sqlRowSet.getInt("category_id");
            String name = sqlRowSet.getString("category_name");
            int optionId = sqlRowSet.getInt("option_id");
            String optionName = sqlRowSet.getString("option_name");

            Option option = new Option(optionId, optionName, findById(id));

            if (categories.contains(new Category(id, name))) {
                Category existing = categories.stream()
                        .filter(category -> category.getId() == id)
                        .findFirst()
                        .orElseThrow(() -> new NotFoundException("Категория не найдена."));
                existing.getOptions().add(option);
            } else {
                Category category = new Category(id, name);
                category.getOptions().add(option);
                categories.add(category);
            }
        }
        return categories;
    }

    @Override
    public Category findById(int id) {
        String sql = "select * from categories where id = ?";
        return jdbcTemplate.query(sql, this::mapRow, id)
                .stream()
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Категория не найдена"));
    }

    @Override
    public Category create(Category category) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("categories")
                .usingGeneratedKeyColumns("id");

        Map<String, Object> map = Map.of("name", category.getName());
        int id = insert.executeAndReturnKey(map).intValue();
        category.setId(id);

        return category;
    }

    public Category mapRow(ResultSet rs, int rowNum) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        return new Category(id, name);
    }
}
