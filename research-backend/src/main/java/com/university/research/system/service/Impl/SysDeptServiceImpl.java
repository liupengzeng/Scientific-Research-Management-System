package com.university.research.system.service.Impl;

import com.university.research.system.domain.SysDept;
import com.university.research.system.mapper.SysDeptMapper;
import com.university.research.system.service.SysDeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 部门服务实现类
 * 
 * @author system
 */
@Service
public class SysDeptServiceImpl implements SysDeptService {

    @Autowired
    private SysDeptMapper deptMapper;

    /**
     * 查询部门列表（仅查询正常状态的部门）
     *
     * @return 部门列表
     */
    @Override
    public List<SysDept> selectDeptList() {
        return deptMapper.selectDeptList();
    }

    @Override
    public List<SysDept> selectDeptTree() {
        List<SysDept> all = deptMapper.selectAll();
        if (all == null || all.isEmpty()) return List.of();
        // 构建树
        Map<Long, SysDept> map = all.stream().collect(Collectors.toMap(SysDept::getDeptId, d -> d, (a, b) -> a));
        List<SysDept> roots = new ArrayList<>();
        for (SysDept d : all) {
            if (d.getParentId() == null || d.getParentId() == 0L) {
                roots.add(d);
            } else {
                SysDept p = map.get(d.getParentId());
                if (p != null) {
                    if (p.getChildren() == null) p.setChildren(new ArrayList<>());
                    p.getChildren().add(d);
                } else {
                    roots.add(d);
                }
            }
        }
        // 排序
        Comparator<SysDept> cmp = Comparator.comparing(dept -> Optional.ofNullable(dept.getOrderNum()).orElse(0));
        roots.sort(cmp);
        roots.forEach(node -> sortChildren(node, cmp));
        return roots;
    }

    private void sortChildren(SysDept node, Comparator<SysDept> cmp) {
        if (node.getChildren() == null || node.getChildren().isEmpty()) return;
        node.getChildren().sort(cmp);
        node.getChildren().forEach(n -> sortChildren(n, cmp));
    }

    @Override
    public SysDept getById(Long deptId) {
        return deptMapper.selectById(deptId);
    }

    @Override
    public int create(SysDept dept) {
        if (dept.getStatus() == null) dept.setStatus("0");
        return deptMapper.insertDept(dept);
    }

    @Override
    public int update(SysDept dept) {
        return deptMapper.updateDept(dept);
    }

    @Override
    public int remove(Long deptId) {
        int children = deptMapper.countChildren(deptId);
        if (children > 0) {
            return 0; // 有子部门，不允许删除
        }
        return deptMapper.deleteDeptById(deptId);
    }

    @Override
    public int changeStatus(Long deptId, String status) {
        return deptMapper.updateStatus(deptId, status);
    }
}

