package com.university.research.research.service.Impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.university.research.research.domain.ResearchProjectCheck;
import com.university.research.research.mapper.ResearchProjectCheckMapper;
import com.university.research.research.service.ResearchProjectCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ResearchProjectCheckServiceImpl implements ResearchProjectCheckService {

    @Autowired
    private ResearchProjectCheckMapper checkMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitCheck(ResearchProjectCheck check) {
        checkMapper.insert(check);
    }

    @Override
    public Page<ResearchProjectCheck> selectPage(Page<ResearchProjectCheck> page, Long projectId) {
        List<ResearchProjectCheck> list = checkMapper.selectCheckList(page, projectId);
        page.setRecords(list);
        return page;
    }
}

