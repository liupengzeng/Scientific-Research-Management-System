package com.university.research.research.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.university.research.common.exception.ServiceException;
import com.university.research.common.utils.SecurityUtils;
import com.university.research.research.domain.CooperationProject;
import com.university.research.research.domain.CooperationProjectApproval;
import com.university.research.research.mapper.CooperationProjectApprovalMapper;
import com.university.research.research.mapper.CooperationProjectMapper;
import com.university.research.research.service.CooperationProjectService;
import com.university.research.system.domain.SysUser;
import com.university.research.system.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class CooperationProjectServiceImpl implements CooperationProjectService {

    private static final String STATUS_DRAFT = "draft";
    private static final String STATUS_SUBMITTED = "submitted";
    private static final String STATUS_APPROVED = "approved";

    private static final String DECISION_APPROVE = "approve";
    private static final String DECISION_REJECT = "reject";

    private static final String ROLE_TEACHER = "teacher";
    private static final List<String> APPROVAL_ROLE_CHAIN = Arrays.asList(
            "research_group_leader", "research_office", "college_leader"
    );

    @Autowired
    private CooperationProjectMapper projectMapper;

    @Autowired
    private CooperationProjectApprovalMapper approvalMapper;

    @Autowired
    private SysUserService userService;

    @Override
    public Page<CooperationProject> selectPage(Page<CooperationProject> page, CooperationProject query) {
        List<CooperationProject> list = projectMapper.selectList(page, query);
        page.setRecords(list);
        return page;
    }

    @Override
    public CooperationProject selectById(Long coopProjectId) {
        return projectMapper.selectById(coopProjectId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createDraft(CooperationProject project) {
        validateBudget(project.getBudget());
        project.setProjectNo(generateProjectNo());
        project.setStatus(STATUS_DRAFT);
        project.setCurrentStep(1);
        project.setCreateTime(LocalDateTime.now());
        projectMapper.insert(project);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(CooperationProject project) {
        if (project.getCoopProjectId() == null) {
            throw new ServiceException("项目ID不能为空");
        }
        validateBudget(project.getBudget());
        projectMapper.updateById(project);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submit(Long coopProjectId) {
        CooperationProject db = projectMapper.selectById(coopProjectId);
        if (db == null) {
            throw new ServiceException("项目不存在");
        }
        if (!STATUS_DRAFT.equals(db.getStatus())) {
            throw new ServiceException("仅草稿状态可提交");
        }
        CooperationProject update = new CooperationProject();
        update.setCoopProjectId(coopProjectId);
        update.setStatus(STATUS_SUBMITTED);
        update.setCurrentStep(1);
        projectMapper.updateById(update);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approve(Long coopProjectId, String decision, String comment) {
        if (!DECISION_APPROVE.equals(decision) && !DECISION_REJECT.equals(decision)) {
            throw new ServiceException("审批结果仅支持approve/reject");
        }
        if (comment == null || comment.trim().isEmpty()) {
            throw new ServiceException("审批意见不能为空");
        }
        CooperationProject project = projectMapper.selectById(coopProjectId);
        if (project == null) {
            throw new ServiceException("项目不存在");
        }
        if (!STATUS_SUBMITTED.equals(project.getStatus())) {
            throw new ServiceException("仅已提交状态可审批");
        }

        String username = SecurityUtils.getUsername();
        if (username == null || username.trim().isEmpty()) {
            throw new ServiceException("未获取到当前登录用户");
        }
        SysUser user = userService.selectUserByUsername(username);
        if (user == null) {
            throw new ServiceException("当前登录用户不存在");
        }
        SysUser fullUser = userService.selectUserById(user.getUserId());
        if (fullUser == null) {
            throw new ServiceException("用户详情不存在");
        }
        Set<String> roleKeys = extractRoleKeys(fullUser);
        if (roleKeys.contains(ROLE_TEACHER)) {
            throw new ServiceException("教师不能执行审批");
        }

        List<CooperationProjectApproval> history = approvalMapper.selectByProjectId(coopProjectId);
        int approvedSteps = (int) history.stream().filter(a -> DECISION_APPROVE.equals(a.getDecision())).count();
        if (approvedSteps >= APPROVAL_ROLE_CHAIN.size()) {
            throw new ServiceException("审批节点已完成");
        }
        String expectedRole = APPROVAL_ROLE_CHAIN.get(approvedSteps);
        if (!roleKeys.contains(expectedRole)) {
            throw new ServiceException("当前节点仅允许角色[" + expectedRole + "]审批");
        }

        CooperationProjectApproval approval = new CooperationProjectApproval();
        approval.setCoopProjectId(coopProjectId);
        approval.setStepNo(approvedSteps + 1);
        approval.setApproverId(fullUser.getUserId());
        approval.setApproverRole(expectedRole);
        approval.setDecision(decision);
        approval.setComment(comment);
        approval.setCreateTime(LocalDateTime.now());

        CooperationProject update = new CooperationProject();
        update.setCoopProjectId(coopProjectId);
        if (DECISION_REJECT.equals(decision)) {
            approval.setFinalFlag(1);
            update.setStatus(STATUS_DRAFT);
            update.setCurrentStep(1);
        } else {
            boolean finalStep = approvedSteps == APPROVAL_ROLE_CHAIN.size() - 1;
            approval.setFinalFlag(finalStep ? 1 : 0);
            update.setStatus(finalStep ? STATUS_APPROVED : STATUS_SUBMITTED);
            update.setCurrentStep(finalStep ? 3 : approvedSteps + 2);
        }

        approvalMapper.insert(approval);
        projectMapper.updateById(update);
    }

    @Override
    public List<CooperationProjectApproval> selectApprovals(Long coopProjectId) {
        return approvalMapper.selectByProjectId(coopProjectId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        LambdaQueryWrapper<CooperationProject> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(CooperationProject::getCoopProjectId, ids);
        projectMapper.delete(wrapper);
    }

    private Set<String> extractRoleKeys(SysUser user) {
        Set<String> roleKeys = new HashSet<>();
        if (user == null || user.getRoles() == null) {
            return roleKeys;
        }
        user.getRoles().forEach(role -> {
            if (role.getRoleKey() != null && !role.getRoleKey().trim().isEmpty()) {
                roleKeys.add(role.getRoleKey().trim().toLowerCase(Locale.ROOT));
            }
        });
        return roleKeys;
    }

    private void validateBudget(BigDecimal budget) {
        BigDecimal val = budget == null ? BigDecimal.ZERO : budget;
        if (val.compareTo(BigDecimal.ZERO) < 0) {
            throw new ServiceException("预算不能为负数");
        }
    }

    private String generateProjectNo() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int rand = ThreadLocalRandom.current().nextInt(100, 999);
        return "COOP" + date + rand;
    }
}
