package com.university.research.research.service;

import com.university.research.research.domain.ResearchProjectMember;

import java.util.List;

public interface ResearchProjectMemberService {

    List<ResearchProjectMember> selectByProjectId(Long projectId);

    void addMember(ResearchProjectMember member);

    void updateMember(ResearchProjectMember member);

    void removeMember(Long id);

    void removeByProjectAndUser(Long projectId, Long userId);
}

