package com.university.research.research.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.university.research.common.exception.ServiceException;
import com.university.research.common.utils.SecurityUtils;
import com.university.research.research.domain.ResearchProject;
import com.university.research.research.domain.ResearchProjectApproval;
import com.university.research.research.domain.ResearchProjectCheck;
import com.university.research.research.domain.ResearchProjectFinal;
import com.university.research.research.mapper.ResearchProjectMapper;
import com.university.research.research.service.ResearchProjectApprovalService;
import com.university.research.research.service.ResearchProjectCheckService;
import com.university.research.research.service.ResearchProjectFinalService;
import com.university.research.research.service.ResearchProjectService;
import com.university.research.system.domain.SysUser;
import com.university.research.system.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class ResearchProjectServiceImpl implements ResearchProjectService {

    private static final String STATUS_DRAFT = "draft";
    private static final String STATUS_SUBMITTED = "submitted";
    private static final String STATUS_APPROVED = "approved";
    private static final String STATUS_IN_PROGRESS = "in_progress";
    private static final String STATUS_MID_CHECK = "mid_check";
    private static final String STATUS_COMPLETED = "completed";
    private static final String STATUS_CLOSED = "closed";

    @Autowired
    private ResearchProjectMapper projectMapper;

    @Autowired
    private ResearchProjectApprovalService approvalService;

    @Autowired
    private SysUserService userService;

    @Autowired
    private ResearchProjectCheckService checkService;

    @Autowired
    private ResearchProjectFinalService finalService;

    @Override
    public Page<ResearchProject> selectPage(Page<ResearchProject> page, ResearchProject query) {
        List<ResearchProject> list = projectMapper.selectProjectList(page, query);
        page.setRecords(list);
        return page;
    }

    @Override
    public ResearchProject selectById(Long projectId) {
        return projectMapper.selectById(projectId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createDraft(ResearchProject project) {
        validateBudget(project);
        project.setProjectStatus(STATUS_DRAFT);
        project.setProjectNo(generateProjectNo());
        projectMapper.insert(project);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProject(ResearchProject project) {
        if (project.getProjectId() == null) {
            throw new ServiceException("项目ID不能为空");
        }
        validateBudget(project);
        projectMapper.updateById(project);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitProject(Long projectId) {
        ResearchProject db = projectMapper.selectById(projectId);
        if (db == null) {
            throw new ServiceException("项目不存在");
        }
        if (!STATUS_DRAFT.equals(db.getProjectStatus())) {
            throw new ServiceException("仅草稿状态可提交");
        }
        ResearchProject update = new ResearchProject();
        update.setProjectId(projectId);
        update.setProjectStatus(STATUS_SUBMITTED);
        projectMapper.updateById(update);
    }

    @Override
    public void startProject(Long projectId) {
        ResearchProject db = projectMapper.selectById(projectId);
        if (db == null) {
            throw new ServiceException("项目不存在");
        }
        if (!STATUS_APPROVED.equals(db.getProjectStatus())) {
            throw new ServiceException("仅立项后的项目可开始");
        }
        ResearchProject update = new ResearchProject();
        update.setProjectId(projectId);
        update.setProjectStatus(STATUS_IN_PROGRESS);
        projectMapper.updateById(update);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        LambdaQueryWrapper<ResearchProject> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(ResearchProject::getProjectId, ids);
        projectMapper.delete(wrapper);
    }

    private void validateBudget(ResearchProject project) {
        BigDecimal total = project.getTotalBudget() == null ? BigDecimal.ZERO : project.getTotalBudget();
        BigDecimal approved = project.getApprovedAmount() == null ? BigDecimal.ZERO : project.getApprovedAmount();
        if (total.compareTo(BigDecimal.ZERO) < 0 || approved.compareTo(BigDecimal.ZERO) < 0) {
            throw new ServiceException("预算金额不能为负数");
        }
        if (approved.compareTo(total) > 0) {
            throw new ServiceException("批准经费不能大于总预算");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approveProject(Long projectId, String decision, String comment) {
        if (comment == null || comment.trim().isEmpty()) {
            throw new ServiceException("审批意见不能为空");
        }

        ResearchProject project = projectMapper.selectById(projectId);
        if (project == null) {
            throw new ServiceException("项目不存在");
        }

        if (!STATUS_SUBMITTED.equals(project.getProjectStatus())) {
            throw new ServiceException("仅已提交状态的项目可以审批");
        }

        // 获取当前审批人信息
        String username = SecurityUtils.getUsername();
        SysUser approver = userService.selectUserByUsername(username);
        if (approver == null) {
            throw new ServiceException("获取审批人信息失败");
        }

        // 查询已有的审批记录，判断是否已有最终审批
        List<ResearchProjectApproval> existingApprovals = approvalService.selectByProjectId(projectId);
        boolean hasFinalApproval = existingApprovals.stream()
                .anyMatch(a -> a.getFinalFlag() != null && a.getFinalFlag() == 1);

        // 如果是多级审批，检查是否所有级别都已审批
        // 这里简化处理：如果已有最终审批，则不允许再次审批
        // 实际多级审批需要根据业务规则判断
        if (hasFinalApproval && "approve".equals(decision)) {
            throw new ServiceException("该项目已完成最终审批，不能重复审批");
        }

        // 创建审批记录
        ResearchProjectApproval approval = new ResearchProjectApproval();
        approval.setProjectId(projectId);
        approval.setDecision(decision);
        approval.setComment(comment);
        approval.setApproverId(approver.getUserId());
        approval.setApproverName(approver.getRealName());
        approval.setCreateTime(LocalDateTime.now());
        approval.setUpdateTime(LocalDateTime.now());

        // 判断是否为最终审批（这里简化：如果通过则设为最终，实际应根据审批级别判断）
        if ("approve".equals(decision)) {
            approval.setFinalFlag(1);
            // 更新项目状态为已立项
            ResearchProject update = new ResearchProject();
            update.setProjectId(projectId);
            update.setProjectStatus(STATUS_APPROVED);
            projectMapper.updateById(update);
        } else if ("reject".equals(decision)) {
            approval.setFinalFlag(1);
            // 驳回后回到草稿状态
            ResearchProject update = new ResearchProject();
            update.setProjectId(projectId);
            update.setProjectStatus(STATUS_DRAFT);
            projectMapper.updateById(update);
        }

        approvalService.insert(approval);
    }

    private String generateProjectNo() {
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd", Locale.CHINA));
        int rand = ThreadLocalRandom.current().nextInt(1000, 9999);
        return "PRJ" + datePart + rand;
    }

    @Override
    public void submitMidCheck(ResearchProjectCheck check) {
        ResearchProject project = projectMapper.selectById(check.getProjectId());
        if (project == null) {
            throw new ServiceException("项目不存在");
        }
        if (!STATUS_IN_PROGRESS.equals(project.getProjectStatus())) {
            throw new ServiceException("仅进行中项目可提交中检");
        }
        if (check.getCheckDate() == null) {
            check.setCheckDate(LocalDate.now());
        }
        check.setResultStatus("pending");
        checkService.submitCheck(check);

        ResearchProject update = new ResearchProject();
        update.setProjectId(check.getProjectId());
        update.setProjectStatus(STATUS_MID_CHECK);
        projectMapper.updateById(update);
    }

    @Override
    public void submitFinal(ResearchProjectFinal fin) {
        ResearchProject project = projectMapper.selectById(fin.getProjectId());
        if (project == null) {
            throw new ServiceException("项目不存在");
        }
        if (!(STATUS_IN_PROGRESS.equals(project.getProjectStatus()) || STATUS_MID_CHECK.equals(project.getProjectStatus()))) {
            throw new ServiceException("仅进行中或中检中的项目可提交结题");
        }
        if (fin.getSubmitDate() == null) {
            fin.setSubmitDate(LocalDate.now());
        }
        fin.setAcceptStatus("pending");
        finalService.submitFinal(fin);

        ResearchProject update = new ResearchProject();
        update.setProjectId(fin.getProjectId());
        update.setProjectStatus(STATUS_COMPLETED);
        projectMapper.updateById(update);
    }

    @Override
    public void acceptFinal(Long projectId, String decision, String comment) {
        ResearchProject project = projectMapper.selectById(projectId);
        if (project == null) {
            throw new ServiceException("项目不存在");
        }
        if (!STATUS_COMPLETED.equals(project.getProjectStatus())) {
            throw new ServiceException("仅已提交结题的项目可验收");
        }
        Long uid = SecurityUtils.getUserId();
        SysUser user = userService.selectUserById(uid);
        String name = user != null ? user.getRealName() : null;

        finalService.acceptFinal(projectId, decision, comment, uid, name);

        ResearchProject update = new ResearchProject();
        update.setProjectId(projectId);
        if ("passed".equals(decision)) {
            update.setProjectStatus(STATUS_CLOSED);
        } else {
            update.setProjectStatus(STATUS_MID_CHECK);
        }
        projectMapper.updateById(update);
    }
}

