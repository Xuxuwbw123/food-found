package com.freshtrace.unified.common;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

@Data
public class PageQuery {
    private Integer pageNum = 1;
    private Integer pageSize = 10;

    public <T> Page<T> toPage() {
        return new Page<>(pageNum, Math.min(pageSize, 100));
    }
}
