package com.freshtrace.unified.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.freshtrace.unified.entity.Recipe;
import org.apache.ibatis.annotations.Mapper;
@Mapper
public interface RecipeMapper extends BaseMapper<Recipe> {
}
