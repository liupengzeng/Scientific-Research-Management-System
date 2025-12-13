package com.university.research.research.controller;

import com.university.research.common.core.AjaxResult;
import com.university.research.common.utils.MinioUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "项目文件上传", description = "MinIO上传接口")
@RestController
@RequestMapping("/api/research/project")
public class ResearchProjectFileController {

    @Autowired
    private MinioUtils minioUtils;

    @Operation(summary = "上传文件到MinIO")
    @PostMapping("/upload")
    @PreAuthorize("hasAuthority('research:project:upload')")
    public AjaxResult<String> upload(@RequestParam("file") MultipartFile file) throws Exception {
        String objectName = minioUtils.upload(file);
        return AjaxResult.success(objectName);
    }
}

