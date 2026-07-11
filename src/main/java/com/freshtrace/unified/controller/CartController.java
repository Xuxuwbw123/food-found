package com.freshtrace.unified.controller;

import com.freshtrace.unified.common.Result;
import com.freshtrace.unified.dto.CartItemDTO;
import com.freshtrace.unified.dto.CartUpdateDTO;
import com.freshtrace.unified.dto.CartVO;
import com.freshtrace.unified.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired private CartService cartService;

    @GetMapping("/list")
    public Result<List<CartVO>> list() {
        return Result.ok(cartService.list());
    }

    @PostMapping("/add")
    public Result<Void> add(@RequestBody CartItemDTO dto) {
        try { cartService.add(dto); return Result.ok(); }
        catch (Exception e) { return Result.fail(e.getMessage()); }
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody CartUpdateDTO dto) {
        try { cartService.update(id, dto); return Result.ok(); }
        catch (Exception e) { return Result.fail(e.getMessage()); }
    }

    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        try { cartService.delete(id); return Result.ok(); }
        catch (Exception e) { return Result.fail(e.getMessage()); }
    }

    @PutMapping("/select-all")
    public Result<Void> selectAll(@RequestParam(defaultValue = "true") Boolean selected) {
        cartService.selectAll(selected);
        return Result.ok();
    }
}
