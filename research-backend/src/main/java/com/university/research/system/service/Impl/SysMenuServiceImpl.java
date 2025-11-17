package com.university.research.system.service.Impl;

import com.university.research.common.exception.ServiceException;
import com.university.research.system.domain.SysMenu;
import com.university.research.system.mapper.SysMenuMapper;
import com.university.research.system.service.SysMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SysMenuServiceImpl implements SysMenuService {

    @Autowired
    private SysMenuMapper menuMapper;

    @Override
    public List<SysMenu> selectMenuTreeByRoleIds(List<Long> roleIds) {
        List<SysMenu> menus;
        if (roleIds == null || roleIds.isEmpty()) {
            menus = menuMapper.selectAllEnabled();
        } else {
            menus = menuMapper.selectByRoleIds(roleIds);
            // 没有关联时兜底返回全部，避免前端空白
            if (menus == null || menus.isEmpty()) {
                menus = menuMapper.selectAllEnabled();
            }
        }
        return buildTree(menus);
    }

    @Override
    public List<SysMenu> selectMenuList(SysMenu menu) {
        return menuMapper.selectMenuList(menu);
    }

    @Override
    public List<SysMenu> selectAllMenuTree() {
        return buildTree(menuMapper.selectAll());
    }

    @Override
    public SysMenu getById(Long menuId) {
        return menuMapper.selectMenuById(menuId);
    }

    @Override
    public int create(SysMenu menu) {
        validateMenu(menu);
        if (menu.getStatus() == null) {
            menu.setStatus("0");
        }
        return menuMapper.insertMenu(menu);
    }

    @Override
    public int update(SysMenu menu) {
        validateMenu(menu);
        if (menu.getParentId() != null && menu.getParentId().equals(menu.getMenuId())) {
            throw new ServiceException("上级菜单不能选择自己");
        }
        return menuMapper.updateMenu(menu);
    }

    @Override
    public int remove(Long menuId) {
        int children = menuMapper.countChildren(menuId);
        if (children > 0) {
            throw new ServiceException("存在子菜单，无法删除");
        }
        menuMapper.deleteRoleMenuByMenuId(menuId);
        return menuMapper.deleteMenuById(menuId);
    }

    @Override
    public int changeStatus(Long menuId, String status) {
        return menuMapper.updateStatus(menuId, status);
    }

    @Override
    public boolean checkMenuNameUnique(String menuName, Long parentId, Long menuId) {
        return menuMapper.checkMenuNameUnique(menuName, parentId, menuId) == 0;
    }

    @Override
    public List<Long> selectMenuIdsByRoleId(Long roleId) {
        return menuMapper.selectMenuIdsByRoleId(roleId);
    }

    @Override
    public void assignMenusToRole(Long roleId, List<Long> menuIds) {
        menuMapper.deleteRoleMenuByRoleId(roleId);
        if (menuIds != null && !menuIds.isEmpty()) {
            menuMapper.batchRoleMenu(roleId, menuIds);
        }
    }

    @Override
    public List<String> selectPermsByRoleIds(List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return Collections.emptyList();
        }
        return menuMapper.selectPermsByRoleIds(roleIds);
    }

    private void validateMenu(SysMenu menu) {
        if (!checkMenuNameUnique(menu.getMenuName(), menu.getParentId(), menu.getMenuId())) {
            throw new ServiceException("同级菜单名称已存在");
        }
    }

    private List<SysMenu> buildTree(List<SysMenu> list) {
        if (list == null || list.isEmpty()) return Collections.emptyList();

        Map<Long, SysMenu> idToNode = list.stream()
                .collect(Collectors.toMap(SysMenu::getMenuId, m -> m, (a, b) -> a, LinkedHashMap::new));
        List<SysMenu> roots = new ArrayList<>();
        for (SysMenu m : list) {
            if (m.getParentId() == null || m.getParentId() == 0L) {
                roots.add(m);
            } else {
                SysMenu parent = idToNode.get(m.getParentId());
                if (parent != null) {
                    if (parent.getChildren() == null) parent.setChildren(new ArrayList<>());
                    parent.getChildren().add(m);
                } else {
                    roots.add(m); // 无父节点时作为根兜底
                }
            }
        }
        // 子节点顺序
        roots.forEach(this::sortChildren);
        roots.sort(Comparator.comparing(m -> Optional.ofNullable(m.getOrderNum()).orElse(0)));
        return roots;
    }

    private void sortChildren(SysMenu node) {
        if (node.getChildren() == null || node.getChildren().isEmpty()) return;
        node.getChildren().sort(Comparator.comparing(m -> Optional.ofNullable(m.getOrderNum()).orElse(0)));
        node.getChildren().forEach(this::sortChildren);
    }
}


