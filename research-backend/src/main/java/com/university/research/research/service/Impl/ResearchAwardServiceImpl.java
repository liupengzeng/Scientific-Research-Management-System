package com.university.research.research.service.Impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.university.research.common.exception.ServiceException;
import com.university.research.research.domain.ResearchAward;
import com.university.research.research.mapper.ResearchAwardMapper;
import com.university.research.research.service.ResearchAwardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ResearchAwardServiceImpl implements ResearchAwardService {

    @Autowired
    private ResearchAwardMapper awardMapper;

    @Override
    public Page<ResearchAward> selectPage(Page<ResearchAward> page, ResearchAward award) {
        List<ResearchAward> list = awardMapper.selectAwardList(award);
        // 手动分页
        int total = list.size();
        int start = (int) ((page.getCurrent() - 1) * page.getSize());
        int end = (int) Math.min(start + page.getSize(), total);

        if (start < total) {
            List<ResearchAward> pageList = list.subList(start, end);
            page.setRecords(pageList);
            page.setTotal(total);
        } else {
            page.setRecords(List.of());
            page.setTotal(total);
        }
        return page;
    }

    @Override
    public ResearchAward selectById(Long awardId) {
        return awardMapper.selectById(awardId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insert(ResearchAward award) {
        if (award.getStatus() == null || award.getStatus().isEmpty()) {
            award.setStatus("active");
        }
        awardMapper.insert(award);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(ResearchAward award) {
        if (award.getAwardId() == null) {
            throw new ServiceException("获奖ID不能为空");
        }
        ResearchAward existing = awardMapper.selectById(award.getAwardId());
        if (existing == null) {
            throw new ServiceException("获奖记录不存在");
        }
        awardMapper.updateById(award);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByIds(Long[] awardIds) {
        if (awardIds == null || awardIds.length == 0) {
            throw new ServiceException("获奖ID不能为空");
        }
        for (Long awardId : awardIds) {
            awardMapper.deleteById(awardId);
        }
    }
}

