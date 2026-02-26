package com.university.research.research.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.university.research.research.domain.CooperationEnterprise;

import java.util.List;

public interface CooperationEnterpriseService {

    Page<CooperationEnterprise> selectPage(Page<CooperationEnterprise> page, CooperationEnterprise query);

    CooperationEnterprise selectById(Long enterpriseId);

    void insert(CooperationEnterprise enterprise);

    void update(CooperationEnterprise enterprise);

    void deleteByIds(List<Long> ids);
}
