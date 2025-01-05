package com.practice.springjdbc.dao;

import com.practice.springjdbc.exception.NotFoundException;
import com.practice.springjdbc.model.Category;
import com.practice.springjdbc.model.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ProductDaoImpl implements ProductDao {
    private final JdbcTemplate jdbcTemplate;
    private final CategoryDao categoryDao;

    @Override
    public List<Product> findAll() {
        String sql = "select * from products";
        return jdbcTemplate.query(sql, this::mapRow);
    }

    @Override
    public Product findById(int id) {
        String sql = "select * from products where id = ?";
        return jdbcTemplate.query(sql, this::mapRow, id)
                .stream()
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Товар не найден"));
    }

    @Override
    public Product create(Product product) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("products")
                .usingGeneratedKeyColumns("id");

        Map<String, Object> map = Map.of(
                "name", product.getName(),
                "price", product.getPrice(),
                "category_id", product.getCategory().getId()
        );
        int id = insert.executeAndReturnKey(map).intValue();
        product.setId(id);

        return product;
    }

    @Override
    public Product update(Product product) {
        String sql = "update products set name = ?, price = ? where id = ?";

        jdbcTemplate.update(sql, product.getName(), product.getPrice(), product.getId());

        return product;
    }

    public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        int price = rs.getInt("price");

        int categoryId = rs.getInt("category_id");
        Category category = categoryDao.findById(categoryId);

        return new Product(id, name, price, category);
    }
}
