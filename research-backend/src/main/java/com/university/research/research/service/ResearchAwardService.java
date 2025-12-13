package com.university.research.research.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.university.research.research.domain.ResearchAward;

public interface ResearchAwardService {

    Page<ResearchAward> selectPage(Page<ResearchAward> page, ResearchAward award);

    ResearchAward selectById(Long awardId);

    void insert(ResearchAward award);

    void update(ResearchAward award);

    void deleteByIds(Long[] awardIds);
}

