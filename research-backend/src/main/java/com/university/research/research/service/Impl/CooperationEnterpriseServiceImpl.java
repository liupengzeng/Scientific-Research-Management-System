package com.university.research.research.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.university.research.common.exception.ServiceException;
import com.university.research.research.domain.CooperationEnterprise;
import com.university.research.research.mapper.CooperationEnterpriseMapper;
import com.university.research.research.service.CooperationEnterpriseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CooperationEnterpriseServiceImpl implements CooperationEnterpriseService {

    @Autowired
    private CooperationEnterpriseMapper enterpriseMapper;

    @Override
    public Page<CooperationEnterprise> selectPage(Page<CooperationEnterprise> page, CooperationEnterprise query) {
        List<CooperationEnterprise> list = enterpriseMapper.selectList(page, query);
        page.setRecords(list);
        return page;
    }

    @Override
    public CooperationEnterprise selectById(Long enterpriseId) {
        return enterpriseMapper.selectById(enterpriseId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insert(CooperationEnterprise enterprise) {
        if (enterprise.getEnterpriseName() == null || enterprise.getEnterpriseName().trim().isEmpty()) {
            throw new ServiceException("企业名称不能为空");
        }
        if (enterprise.getStatus() == null || enterprise.getStatus().trim().isEmpty()) {
            enterprise.setStatus("0");
        }
        enterprise.setCreateTime(LocalDateTime.now());
        enterpriseMapper.insert(enterprise);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(CooperationEnterprise enterprise) {
        if (enterprise.getEnterpriseId() == null) {
            throw new ServiceException("企业ID不能为空");
        }
        enterpriseMapper.updateById(enterprise);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        LambdaQueryWrapper<CooperationEnterprise> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(CooperationEnterprise::getEnterpriseId, ids);
        enterpriseMapper.delete(wrapper);
    }
}
