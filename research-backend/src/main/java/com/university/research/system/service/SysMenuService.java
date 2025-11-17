package com.university.research.system.service;

import com.university.research.system.domain.SysMenu;

import java.util.List;

public interface SysMenuService {
    /**
     * 根据角色ID集合查询菜单树；为空则返回全部
     */
    List<SysMenu> selectMenuTreeByRoleIds(List<Long> roleIds);

    /**
     * 查询菜单列表
     */
    List<SysMenu> selectMenuList(SysMenu menu);

    /**
     * 查询全部菜单树
     */
    List<SysMenu> selectAllMenuTree();

    SysMenu getById(Long menuId);

    int create(SysMenu menu);

    int update(SysMenu menu);

    int remove(Long menuId);

    int changeStatus(Long menuId, String status);

    boolean checkMenuNameUnique(String menuName, Long parentId, Long menuId);

    List<Long> selectMenuIdsByRoleId(Long roleId);

    void assignMenusToRole(Long roleId, List<Long> menuIds);

    List<String> selectPermsByRoleIds(List<Long> roleIds);
}
