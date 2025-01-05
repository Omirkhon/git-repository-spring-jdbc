package com.practice.springjdbc.dao;

import com.practice.springjdbc.model.Category;

import java.util.List;

public interface CategoryDao {
    List<Category> findAll();
    Category findById(int id);
    Category create(Category category);
}
