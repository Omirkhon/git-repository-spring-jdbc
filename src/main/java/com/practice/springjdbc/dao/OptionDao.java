package com.practice.springjdbc.dao;

import com.practice.springjdbc.model.Option;
import java.util.List;

public interface OptionDao {
    List<Option> findAll();
    Option findById(int id);
    Option create(Option option);
}
