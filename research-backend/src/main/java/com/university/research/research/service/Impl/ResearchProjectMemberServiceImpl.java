package com.university.research.research.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.university.research.common.exception.ServiceException;
import com.university.research.research.domain.ResearchProjectMember;
import com.university.research.research.mapper.ResearchProjectMemberMapper;
import com.university.research.research.service.ResearchProjectMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class ResearchProjectMemberServiceImpl implements ResearchProjectMemberService {

    @Autowired
    private ResearchProjectMemberMapper memberMapper;

    @Override
    public List<ResearchProjectMember> selectByProjectId(Long projectId) {
        return memberMapper.selectMemberList(projectId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addMember(ResearchProjectMember member) {
        if (member.getProjectId() == null) {
            throw new ServiceException("项目ID不能为空");
        }
        if (member.getUserId() == null) {
            throw new ServiceException("用户ID不能为空");
        }

        // 检查是否已存在
        LambdaQueryWrapper<ResearchProjectMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ResearchProjectMember::getProjectId, member.getProjectId())
                .eq(ResearchProjectMember::getUserId, member.getUserId());
        long count = memberMapper.selectCount(wrapper);
        if (count > 0) {
            throw new ServiceException("该用户已是项目成员，不能重复添加");
        }

        // 设置默认加入日期
        if (member.getJoinDate() == null) {
            member.setJoinDate(LocalDate.now());
        }

        memberMapper.insert(member);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMember(ResearchProjectMember member) {
        if (member.getId() == null) {
            throw new ServiceException("成员ID不能为空");
        }
        memberMapper.updateById(member);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeMember(Long id) {
        if (id == null) {
            throw new ServiceException("成员ID不能为空");
        }
        memberMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeByProjectAndUser(Long projectId, Long userId) {
        LambdaQueryWrapper<ResearchProjectMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ResearchProjectMember::getProjectId, projectId)
                .eq(ResearchProjectMember::getUserId, userId);
        memberMapper.delete(wrapper);
    }
}

