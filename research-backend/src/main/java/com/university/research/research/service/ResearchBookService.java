package com.university.research.research.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.university.research.research.domain.ResearchBook;

public interface ResearchBookService {

    Page<ResearchBook> selectPage(Page<ResearchBook> page, ResearchBook book);

    ResearchBook selectById(Long bookId);

    void insert(ResearchBook book);

    void update(ResearchBook book);

    void deleteByIds(Long[] bookIds);
}

