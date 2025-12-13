package com.university.research.research.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.university.research.research.domain.ResearchPaper;

public interface ResearchPaperService {

    Page<ResearchPaper> selectPage(Page<ResearchPaper> page, ResearchPaper paper);

    ResearchPaper selectById(Long paperId);

    void createDraft(ResearchPaper paper);

    void updatePaper(ResearchPaper paper);

    void submitPaper(Long paperId);

    void approvePaper(Long paperId, String decision, String comment, Integer finalFlag);

    void deleteByIds(Long[] paperIds);
}

