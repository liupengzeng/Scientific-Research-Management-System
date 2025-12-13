package com.university.research.research.service.Impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.university.research.common.exception.ServiceException;
import com.university.research.research.domain.ResearchBook;
import com.university.research.research.mapper.ResearchBookMapper;
import com.university.research.research.service.ResearchBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ResearchBookServiceImpl implements ResearchBookService {

    @Autowired
    private ResearchBookMapper bookMapper;

    @Override
    public Page<ResearchBook> selectPage(Page<ResearchBook> page, ResearchBook book) {
        List<ResearchBook> list = bookMapper.selectBookList(book);
        // 手动分页
        int total = list.size();
        int start = (int) ((page.getCurrent() - 1) * page.getSize());
        int end = (int) Math.min(start + page.getSize(), total);

        if (start < total) {
            List<ResearchBook> pageList = list.subList(start, end);
            page.setRecords(pageList);
            page.setTotal(total);
        } else {
            page.setRecords(List.of());
            page.setTotal(total);
        }
        return page;
    }

    @Override
    public ResearchBook selectById(Long bookId) {
        return bookMapper.selectById(bookId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insert(ResearchBook book) {
        if (book.getStatus() == null || book.getStatus().isEmpty()) {
            book.setStatus("active");
        }
        bookMapper.insert(book);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(ResearchBook book) {
        if (book.getBookId() == null) {
            throw new ServiceException("著作ID不能为空");
        }
        ResearchBook existing = bookMapper.selectById(book.getBookId());
        if (existing == null) {
            throw new ServiceException("著作不存在");
        }
        bookMapper.updateById(book);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByIds(Long[] bookIds) {
        if (bookIds == null || bookIds.length == 0) {
            throw new ServiceException("著作ID不能为空");
        }
        for (Long bookId : bookIds) {
            bookMapper.deleteById(bookId);
        }
    }
}

