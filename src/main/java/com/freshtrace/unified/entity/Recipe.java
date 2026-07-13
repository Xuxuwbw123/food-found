package com.freshtrace.unified.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Data
@TableName("recipe")
public class Recipe {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private String title;
    private String description;
    private String coverImage;
    private String ingredients;
    private String steps;
    private Integer difficulty;
    private Integer cookTime;
    private String tags;
    private Integer status;
    private java.time.LocalDateTime createTime;
}
