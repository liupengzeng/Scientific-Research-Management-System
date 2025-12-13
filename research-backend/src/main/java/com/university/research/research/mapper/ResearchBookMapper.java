package com.university.research.research.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.university.research.research.domain.ResearchBook;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ResearchBookMapper extends BaseMapper<ResearchBook> {

    List<ResearchBook> selectBookList(@Param("book") ResearchBook book);
}

