package com.freshtrace.unified.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@Data
@TableName("recipe_ingredient")
public class RecipeIngredient {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long recipeId;
    private Long productId;
    private String quantity;
    private Integer sort;
}
