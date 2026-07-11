package com.freshtrace.unified.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.freshtrace.unified.entity.Traceability;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface TraceabilityMapper extends BaseMapper<Traceability> {

    // 查询待删除审核的溯源记录（绕过 @TableLogic）
    @Select("SELECT * FROM traceability WHERE deleted = 2 ORDER BY create_time DESC")
    List<Traceability> selectPendingDeletes();

    // 设置删除状态为待审核（绕过 @TableLogic）
    @Update("UPDATE traceability SET deleted = #{status} WHERE id = #{id}")
    int updateDeletedStatus(@Param("id") Long id, @Param("status") Integer status);
}
