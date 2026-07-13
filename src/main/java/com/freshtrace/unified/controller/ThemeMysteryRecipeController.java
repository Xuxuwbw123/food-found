package com.freshtrace.unified.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.freshtrace.unified.common.Result;
import com.freshtrace.unified.entity.*;
import com.freshtrace.unified.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
public class ThemeMysteryRecipeController {

    @Autowired private ThemeZoneService themeService;
    @Autowired private ThemeGoodsService themeGoodsService;
    @Autowired private MysteryBoxService mysteryService;
    @Autowired private RecipeService recipeService;
    @Autowired private RecipeIngredientService ingredientService;
    @Autowired private ProductService productService;

    // ============ 主题专区 ============
    @GetMapping("/admin/themes")
    public Result<?> themeList() { return Result.success(themeService.list()); }

    @PostMapping("/admin/themes")
    public Result<?> themeAdd(@RequestBody ThemeZone t) { t.setCreateTime(LocalDateTime.now()); themeService.save(t); return Result.success(); }

    @PutMapping("/admin/themes")
    public Result<?> themeUpdate(@RequestBody ThemeZone t) { themeService.updateById(t); return Result.success(); }

    @DeleteMapping("/admin/themes/{id}")
    public Result<?> themeDelete(@PathVariable Long id) { themeService.removeById(id); return Result.success(); }

    @PostMapping("/admin/themes/{id}/products")
    public Result<?> addThemeProduct(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        List<Long> ids = ((List<Number>) body.get("productIds")).stream().map(Number::longValue).toList();
        for (Long pid : ids) {
            ThemeGoods tg = new ThemeGoods(); tg.setThemeId(id); tg.setProductId(pid); tg.setSort(0);
            themeGoodsService.save(tg);
        }
        return Result.success();
    }

    @GetMapping("/api/themes/active")
    public Result<?> activeThemes() {
        LocalDateTime now = LocalDateTime.now();
        return Result.success(themeService.list(new LambdaQueryWrapper<ThemeZone>()
                .eq(ThemeZone::getStatus, 1)
                .le(ThemeZone::getStartTime, now).ge(ThemeZone::getEndTime, now)));
    }

    @GetMapping("/api/themes/{id}/products")
    public Result<?> themeProducts(@PathVariable Long id) {
        List<ThemeGoods> goods = themeGoodsService.list(new LambdaQueryWrapper<ThemeGoods>()
                .eq(ThemeGoods::getThemeId, id));
        List<Product> products = new ArrayList<>();
        for (ThemeGoods tg : goods) {
            Product p = productService.getById(tg.getProductId());
            if (p != null) products.add(p);
        }
        return Result.success(products);
    }

    // ============ 盲盒 ============
    @GetMapping("/admin/mystery-boxes")
    public Result<?> mysteryList() { return Result.success(mysteryService.list()); }

    @PostMapping("/admin/mystery-boxes")
    public Result<?> mysteryAdd(@RequestBody MysteryBox m) { m.setCreateTime(LocalDateTime.now()); mysteryService.save(m); return Result.success(); }

    @PutMapping("/admin/mystery-boxes")
    public Result<?> mysteryUpdate(@RequestBody MysteryBox m) { mysteryService.updateById(m); return Result.success(); }

    @DeleteMapping("/admin/mystery-boxes/{id}")
    public Result<?> mysteryDelete(@PathVariable Long id) { mysteryService.removeById(id); return Result.success(); }

    @GetMapping("/api/mystery-boxes")
    public Result<?> mysteryUserList() {
        return Result.success(mysteryService.list(new LambdaQueryWrapper<MysteryBox>()
                .eq(MysteryBox::getStatus, 1)));
    }

    // ============ 菜谱 ============
    @GetMapping("/admin/recipes")
    public Result<?> recipeList() { return Result.success(recipeService.list()); }

    @PostMapping("/admin/recipes")
    public Result<?> recipeAdd(@RequestBody Recipe r) { r.setCreateTime(LocalDateTime.now()); recipeService.save(r); return Result.success(); }

    @PutMapping("/admin/recipes")
    public Result<?> recipeUpdate(@RequestBody Recipe r) { recipeService.updateById(r); return Result.success(); }

    @DeleteMapping("/admin/recipes/{id}")
    public Result<?> recipeDelete(@PathVariable Long id) { recipeService.removeById(id); return Result.success(); }

    @GetMapping("/api/recipes")
    public Result<?> recipeUserList() {
        return Result.success(recipeService.list(new LambdaQueryWrapper<Recipe>()
                .eq(Recipe::getStatus, 1)));
    }

    @GetMapping("/api/recipes/{id}")
    public Result<?> recipeDetail(@PathVariable Long id) {
        Recipe recipe = recipeService.getById(id);
        if (recipe == null) return Result.error(404, "菜谱不存在");
        Map<String, Object> data = new HashMap<>();
        data.put("recipe", recipe);
        data.put("ingredients", ingredientService.list(new LambdaQueryWrapper<RecipeIngredient>()
                .eq(RecipeIngredient::getRecipeId, id)));
        return Result.success(data);
    }

    @GetMapping("/api/recipes/by-product/{productId}")
    public Result<?> recipesByProduct(@PathVariable Long productId) {
        List<RecipeIngredient> ingredients = ingredientService.list(new LambdaQueryWrapper<RecipeIngredient>()
                .eq(RecipeIngredient::getProductId, productId));
        List<Recipe> recipes = new ArrayList<>();
        for (RecipeIngredient ri : ingredients) {
            Recipe r = recipeService.getById(ri.getRecipeId());
            if (r != null) recipes.add(r);
        }
        return Result.success(recipes);
    }
}
