package com.university.research.research.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.university.research.common.exception.ServiceException;
import com.university.research.research.domain.ResearchProjectType;
import com.university.research.research.mapper.ResearchProjectTypeMapper;
import com.university.research.research.service.ResearchProjectTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ResearchProjectTypeServiceImpl implements ResearchProjectTypeService {

    @Autowired
    private ResearchProjectTypeMapper typeMapper;

    @Override
    public Page<ResearchProjectType> selectPage(Page<ResearchProjectType> page, ResearchProjectType query) {
        LambdaQueryWrapper<ResearchProjectType> wrapper = buildWrapper(query);
        return typeMapper.selectPage(page, wrapper);
    }

    @Override
    public ResearchProjectType selectById(Long typeId) {
        return typeMapper.selectById(typeId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(ResearchProjectType type) {
        if (!checkNameUnique(type.getTypeName(), null)) {
            throw new ServiceException("项目类型名称已存在");
        }
        if (!checkCodeUnique(type.getTypeCode(), null)) {
            throw new ServiceException("项目类型编码已存在");
        }
        if (type.getOrderNum() == null) {
            type.setOrderNum(0);
        }
        typeMapper.insert(type);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(ResearchProjectType type) {
        if (type.getTypeId() == null) {
            throw new ServiceException("类型ID不能为空");
        }
        if (!checkNameUnique(type.getTypeName(), type.getTypeId())) {
            throw new ServiceException("项目类型名称已存在");
        }
        if (!checkCodeUnique(type.getTypeCode(), type.getTypeId())) {
            throw new ServiceException("项目类型编码已存在");
        }
        typeMapper.updateById(type);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        LambdaQueryWrapper<ResearchProjectType> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(ResearchProjectType::getTypeId, ids);
        typeMapper.delete(wrapper);
    }

    @Override
    public boolean checkNameUnique(String typeName, Long typeId) {
        if (typeName == null || typeName.isEmpty()) {
            return true;
        }
        LambdaQueryWrapper<ResearchProjectType> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ResearchProjectType::getTypeName, typeName);
        if (typeId != null) {
            wrapper.ne(ResearchProjectType::getTypeId, typeId);
        }
        Long count = typeMapper.selectCount(wrapper);
        return count == 0;
    }

    @Override
    public boolean checkCodeUnique(String typeCode, Long typeId) {
        if (typeCode == null || typeCode.isEmpty()) {
            return true;
        }
        LambdaQueryWrapper<ResearchProjectType> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ResearchProjectType::getTypeCode, typeCode);
        if (typeId != null) {
            wrapper.ne(ResearchProjectType::getTypeId, typeId);
        }
        Long count = typeMapper.selectCount(wrapper);
        return count == 0;
    }

    private LambdaQueryWrapper<ResearchProjectType> buildWrapper(ResearchProjectType query) {
        LambdaQueryWrapper<ResearchProjectType> wrapper = new LambdaQueryWrapper<>();
        if (query == null) {
            return wrapper.orderByAsc(ResearchProjectType::getOrderNum).orderByAsc(ResearchProjectType::getTypeId);
        }
        wrapper.like(query.getTypeName() != null && !query.getTypeName().isEmpty(), ResearchProjectType::getTypeName, query.getTypeName());
        wrapper.eq(query.getStatus() != null && !query.getStatus().isEmpty(), ResearchProjectType::getStatus, query.getStatus());
        wrapper.eq(query.getTypeCode() != null && !query.getTypeCode().isEmpty(), ResearchProjectType::getTypeCode, query.getTypeCode());
        if (query.getBeginTime() != null) {
            wrapper.ge(ResearchProjectType::getCreateTime, query.getBeginTime());
        }
        if (query.getEndTime() != null) {
            wrapper.le(ResearchProjectType::getCreateTime, query.getEndTime());
        }
        wrapper.orderByAsc(ResearchProjectType::getOrderNum).orderByAsc(ResearchProjectType::getTypeId);
        return wrapper;
    }
}

