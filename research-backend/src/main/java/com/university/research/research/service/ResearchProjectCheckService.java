package com.university.research.research.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.university.research.research.domain.ResearchProjectCheck;

public interface ResearchProjectCheckService {

    void submitCheck(ResearchProjectCheck check);

    Page<ResearchProjectCheck> selectPage(Page<ResearchProjectCheck> page, Long projectId);
}

