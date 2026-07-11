package com.freshtrace.unified.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.freshtrace.unified.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
