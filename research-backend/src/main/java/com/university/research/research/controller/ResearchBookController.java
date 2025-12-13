package com.university.research.research.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.university.research.common.annotation.Log;
import com.university.research.common.core.AjaxResult;
import com.university.research.common.core.PageQuery;
import com.university.research.common.core.PageResult;
import com.university.research.research.domain.ResearchBook;
import com.university.research.research.service.ResearchBookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "著作管理", description = "著作管理相关接口")
@RestController
@RequestMapping("/research/book")
public class ResearchBookController {

    @Autowired
    private ResearchBookService bookService;

    @Operation(summary = "分页查询著作列表")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('research:book:list')")
    public AjaxResult<PageResult<ResearchBook>> list(ResearchBook book, PageQuery pageQuery) {
        pageQuery.checkPage();
        Page<ResearchBook> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());
        Page<ResearchBook> result = bookService.selectPage(page, book);
        return AjaxResult.page(PageResult.of(result));
    }

    @Operation(summary = "查询著作详情")
    @GetMapping("/{bookId}")
    @PreAuthorize("hasAuthority('research:book:query')")
    public AjaxResult<ResearchBook> getInfo(@PathVariable Long bookId) {
        return AjaxResult.success(bookService.selectById(bookId));
    }

    @Operation(summary = "新增著作")
    @PostMapping
    @PreAuthorize("hasAuthority('research:book:add')")
    @Log(title = "著作管理", businessType = 1)
    public AjaxResult<Void> add(@Validated @RequestBody ResearchBook book) {
        bookService.insert(book);
        return AjaxResult.success();
    }

    @Operation(summary = "更新著作")
    @PutMapping
    @PreAuthorize("hasAuthority('research:book:edit')")
    @Log(title = "著作管理", businessType = 2)
    public AjaxResult<Void> edit(@Validated @RequestBody ResearchBook book) {
        bookService.update(book);
        return AjaxResult.success();
    }

    @Operation(summary = "删除著作")
    @DeleteMapping("/{bookIds}")
    @PreAuthorize("hasAuthority('research:book:remove')")
    @Log(title = "著作管理", businessType = 3)
    public AjaxResult<Void> remove(@PathVariable Long[] bookIds) {
        bookService.deleteByIds(bookIds);
        return AjaxResult.success();
    }
}

