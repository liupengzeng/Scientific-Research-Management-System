package com.university.research.research.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.university.research.research.domain.ResearchPatent;

public interface ResearchPatentService {

    Page<ResearchPatent> selectPage(Page<ResearchPatent> page, ResearchPatent patent);

    ResearchPatent selectById(Long patentId);

    void createDraft(ResearchPatent patent);

    void updatePatent(ResearchPatent patent);

    void submitPatent(Long patentId);

    void approvePatent(Long patentId, String decision, String comment, Integer finalFlag);

    void deleteByIds(Long[] patentIds);
}

