package com.freshtrace.unified.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freshtrace.unified.entity.Recipe;
import com.freshtrace.unified.mapper.RecipeMapper;
import com.freshtrace.unified.service.RecipeService;
import org.springframework.stereotype.Service;
@Service
public class RecipeServiceImpl extends ServiceImpl<RecipeMapper, Recipe> implements RecipeService {
}
