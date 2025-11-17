package com.university.research.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.university.research.system.domain.SysMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysMenuMapper extends BaseMapper<SysMenu> {
    /**
     * 查询所有正常状态菜单
     */
    List<SysMenu> selectAllEnabled();

    /**
     * 根据角色ID集合查询菜单（最小实现：超级管理员返回全部）
     */
    List<SysMenu> selectByRoleIds(@Param("roleIds") List<Long> roleIds);

    /**
     * 查询菜单列表（包含过滤条件）
     */
    List<SysMenu> selectMenuList(SysMenu menu);

    /**
     * 查询所有菜单
     */
    List<SysMenu> selectAll();

    /**
     * 根据ID查询菜单
     */
    SysMenu selectMenuById(Long menuId);

    int insertMenu(SysMenu menu);

    int updateMenu(SysMenu menu);

    int deleteMenuById(Long menuId);

    int updateStatus(@Param("menuId") Long menuId, @Param("status") String status);

    int countChildren(Long menuId);

    int checkMenuNameUnique(@Param("menuName") String menuName, @Param("parentId") Long parentId, @Param("menuId") Long menuId);

    List<Long> selectMenuIdsByRoleId(Long roleId);

    int deleteRoleMenuByRoleId(Long roleId);

    int batchRoleMenu(@Param("roleId") Long roleId, @Param("menuIds") List<Long> menuIds);

    int deleteRoleMenuByMenuId(Long menuId);

    List<String> selectPermsByRoleIds(@Param("roleIds") List<Long> roleIds);
}


