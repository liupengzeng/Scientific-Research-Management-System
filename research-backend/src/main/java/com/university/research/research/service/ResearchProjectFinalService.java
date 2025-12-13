package com.university.research.research.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.university.research.research.domain.ResearchProjectFinal;

public interface ResearchProjectFinalService {

    void submitFinal(ResearchProjectFinal fin);

    void acceptFinal(Long projectId, String decision, String comment, Long acceptorId, String acceptorName);

    Page<ResearchProjectFinal> selectPage(Page<ResearchProjectFinal> page, Long projectId);
}

