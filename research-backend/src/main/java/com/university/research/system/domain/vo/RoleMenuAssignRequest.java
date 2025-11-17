package com.university.research.system.domain.vo;

import lombok.Data;

import java.util.List;

@Data
public class RoleMenuAssignRequest {
    private Long roleId;
    private List<Long> menuIds;
}

