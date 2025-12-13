package com.university.research.research.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.university.research.research.domain.ResearchProjectType;

import java.util.List;

public interface ResearchProjectTypeService {

    Page<ResearchProjectType> selectPage(Page<ResearchProjectType> page, ResearchProjectType query);

    ResearchProjectType selectById(Long typeId);

    void create(ResearchProjectType type);

    void update(ResearchProjectType type);

    void deleteByIds(List<Long> ids);

    boolean checkNameUnique(String typeName, Long typeId);

    boolean checkCodeUnique(String typeCode, Long typeId);
}

