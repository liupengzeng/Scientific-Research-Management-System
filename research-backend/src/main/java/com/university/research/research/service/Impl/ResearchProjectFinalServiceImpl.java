package com.university.research.research.service.Impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.university.research.common.exception.ServiceException;
import com.university.research.research.domain.ResearchProjectFinal;
import com.university.research.research.mapper.ResearchProjectFinalMapper;
import com.university.research.research.service.ResearchProjectFinalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class ResearchProjectFinalServiceImpl implements ResearchProjectFinalService {

    @Autowired
    private ResearchProjectFinalMapper finalMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitFinal(ResearchProjectFinal fin) {
        if (fin.getSubmitDate() == null) {
            fin.setSubmitDate(LocalDate.now());
        }
        finalMapper.insert(fin);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void acceptFinal(Long projectId, String decision, String comment, Long acceptorId, String acceptorName) {
        if (comment == null || comment.trim().isEmpty()) {
            throw new ServiceException("验收意见不能为空");
        }
        ResearchProjectFinal latest = getLatestFinal(projectId);
        if (latest == null) {
            throw new ServiceException("未找到结题记录");
        }
        ResearchProjectFinal update = new ResearchProjectFinal();
        update.setId(latest.getId());
        update.setAcceptStatus(decision);
        update.setComment(comment);
        update.setAcceptorId(acceptorId);
        update.setAcceptorName(acceptorName);
        update.setAcceptDate(LocalDate.now());
        finalMapper.updateById(update);
    }

    @Override
    public Page<ResearchProjectFinal> selectPage(Page<ResearchProjectFinal> page, Long projectId) {
        List<ResearchProjectFinal> list = finalMapper.selectFinalList(page, projectId);
        page.setRecords(list);
        return page;
    }

    private ResearchProjectFinal getLatestFinal(Long projectId) {
        Page<ResearchProjectFinal> page = new Page<>(1, 1);
        List<ResearchProjectFinal> list = finalMapper.selectFinalList(page, projectId);
        return list != null && !list.isEmpty() ? list.get(0) : null;
    }
}

