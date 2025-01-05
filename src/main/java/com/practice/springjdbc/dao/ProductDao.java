package com.practice.springjdbc.dao;

import com.practice.springjdbc.model.Product;

import java.util.List;

public interface ProductDao {
    List<Product> findAll();
    Product findById(int id);
    Product create(Product product);
    Product update(Product product);
}
