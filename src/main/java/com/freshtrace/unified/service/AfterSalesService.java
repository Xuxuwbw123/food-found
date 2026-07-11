package com.freshtrace.unified.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.freshtrace.unified.dto.AfterSalesDTO;
import com.freshtrace.unified.dto.AfterSalesUpdateDTO;
import com.freshtrace.unified.dto.AfterSalesVO;

public interface AfterSalesService {
    void apply(AfterSalesDTO dto);
    Page<AfterSalesVO> list(Integer pageNum, Integer pageSize);
    AfterSalesVO getDetail(Long id);
    void update(Long id, AfterSalesUpdateDTO dto);
}
