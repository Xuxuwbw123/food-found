package com.freshtrace.unified.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freshtrace.unified.entity.RecipeIngredient;
import com.freshtrace.unified.mapper.RecipeIngredientMapper;
import com.freshtrace.unified.service.RecipeIngredientService;
import org.springframework.stereotype.Service;
@Service
public class RecipeIngredientServiceImpl extends ServiceImpl<RecipeIngredientMapper, RecipeIngredient> implements RecipeIngredientService {
}
