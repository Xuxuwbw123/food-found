package com.freshtrace.unified.controller;

import com.freshtrace.unified.common.Result;
import com.freshtrace.unified.dto.AfterSalesDTO;
import com.freshtrace.unified.dto.AfterSalesUpdateDTO;
import com.freshtrace.unified.dto.AfterSalesVO;
import com.freshtrace.unified.service.AfterSalesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

@RestController
@RequestMapping("/api/after-sales")
public class AfterSalesController {

    @Autowired private AfterSalesService afterSalesService;

    @PostMapping("/apply")
    public Result<Void> apply(@RequestBody AfterSalesDTO dto) {
        try { afterSalesService.apply(dto); return Result.ok(); }
        catch (Exception e) { return Result.fail(e.getMessage()); }
    }

    @GetMapping
    public Result<Page<AfterSalesVO>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.ok(afterSalesService.list(pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public Result<AfterSalesVO> detail(@PathVariable Long id) {
        try { return Result.ok(afterSalesService.getDetail(id)); }
        catch (Exception e) { return Result.fail(e.getMessage()); }
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody AfterSalesUpdateDTO dto) {
        try { afterSalesService.update(id, dto); return Result.ok(); }
        catch (Exception e) { return Result.fail(e.getMessage()); }
    }
}
